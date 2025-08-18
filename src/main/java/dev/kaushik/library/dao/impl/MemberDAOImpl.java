package dev.kaushik.library.dao.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import dev.kaushik.library.dao.MemberDAO;
import dev.kaushik.library.model.Member;
import dev.kaushik.library.model.enums.Gender;

@Repository
public class MemberDAOImpl implements MemberDAO{

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

	
	private final RowMapper<Member> memberRowMapper= (rs,rowNum) -> 
	    Member.builder()
	        .memberID(rs.getInt("memberID"))
	        .name(rs.getString("name"))
	        .email(rs.getString("email"))
	        .phoneNumber(rs.getLong("phoneNumber"))
	        .gender(Gender.fromCode(rs.getString("gender").charAt(0)))
	        .address(rs.getString("address"))
	        .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
	        .createdBy(rs.getString("created_by"))
	        .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
	        .updatedBy(rs.getString("updated_by"))
	        .build();
	
	@Override
	public int addMember(Member member) throws DataAccessException {
		String sql = "INSERT INTO members (name,email,phoneNumber,gender,address,created_by) "
				+ "VALUES (:name,:email,:phoneNumber,:gender,:address,:createdBy)";
		
		MapSqlParameterSource params=new MapSqlParameterSource()
				.addValue("name", member.getName())
	            .addValue("email", member.getEmail())
	            .addValue("phoneNumber", member.getPhoneNumber())
	            .addValue("gender", String.valueOf(member.getGender().getCode()))
	            .addValue("address", member.getAddress())
	            .addValue("createdBy", "ADMIN");
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "memberID" });
		return keyHolder.getKey() != null ? keyHolder.getKey().intValue() : -1;
	}

	@Override
	public boolean updateMember(Member member) throws DataAccessException {
		List<Member> existingMembers = findMembers(Member.builder().memberID(member.getMemberID()).build());

		if (existingMembers.isEmpty()) {
			return false;
		}

		Member oldMember = existingMembers.get(0);

		String sql = "UPDATE members SET name=:name, email=:email, phoneNumber=:phoneNumber,"
				+ "gender=:gender, address=:address, updated_by=:updatedBy, updated_at=CURRENT_TIMESTAMP "
				+ "WHERE memberID=:memberID";

		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("name", member.getName())
				.addValue("email", member.getEmail())
				.addValue("phoneNumber", member.getPhoneNumber())
				.addValue("gender", String.valueOf(member.getGender().getCode()))
				.addValue("address", member.getAddress())
				.addValue("updatedBy", member.getUpdatedBy())
				.addValue("memberID", member.getMemberID());

		int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
		
		if (rowsAffected > 0) {
			logMemberChange(oldMember);
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteMember(int memberId) throws DataAccessException {
		List<Member> existingMembers = findMembers(Member.builder().memberID(memberId).build());

		if (existingMembers.isEmpty()) {
			return false;
		}

		Member oldMember = existingMembers.get(0);
		
		String sql="DELETE FROM members WHERE memberID=:memberId";
		MapSqlParameterSource params=new MapSqlParameterSource("memberId",memberId);
		int rowsAffected = namedParameterJdbcTemplate.update(sql,params);
		
		if (rowsAffected > 0) {
			logMemberChange(oldMember);
			return true;
		}
		return false;
	}

	@Override
	public int deleteMembersInBatch(Integer... memberIds) throws DataAccessException {
		if (memberIds == null || memberIds.length == 0) {
			return 0;
		}
		List<Integer> memberIdList = Arrays.asList(memberIds);

		List<Member> membersToLog = findMembers(Member.builder().build()).stream()
			.filter(m -> memberIdList.contains(m.getMemberID()))
			.collect(Collectors.toList());

		String query = "DELETE FROM members WHERE memberID IN (:memberIds)";
		MapSqlParameterSource params = new MapSqlParameterSource("memberIds", memberIdList);
		int rowsAffected = namedParameterJdbcTemplate.update(query, params);

		if (rowsAffected > 0) {
			membersToLog.forEach(this::logMemberChange);
		}
		return rowsAffected;
	}

	@Override
	public List<Member> findMembers(Member criteria) throws DataAccessException {
	    StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM members WHERE 1=1");
	    MapSqlParameterSource params = new MapSqlParameterSource();

		if (criteria != null) {
			if (criteria.getMemberID() != null && criteria.getMemberID() != 0) {
	            sqlBuilder.append(" AND memberID=:memberID");
	            params.addValue("memberID", criteria.getMemberID());
	        }
	        if (criteria.getName() != null && !criteria.getName().trim().isEmpty()) {
	            sqlBuilder.append(" AND name LIKE :name");
	            params.addValue("name", "%" + criteria.getName().trim() + "%");
	        }
	        if (criteria.getEmail() != null && !criteria.getEmail().trim().isEmpty()) {
	            sqlBuilder.append(" AND email LIKE :email");
	            params.addValue("email", "%" + criteria.getEmail().trim() + "%");
	        }
	        if (criteria.getPhoneNumber() != 0) {
	            sqlBuilder.append(" AND phoneNumber=:phoneNumber");
	            params.addValue("phoneNumber", criteria.getPhoneNumber());
	        }
	        if (criteria.getGender() != null) {
	            sqlBuilder.append(" AND gender=:gender");
	            params.addValue("gender", String.valueOf(criteria.getGender().getCode()));
	        }
	        if (criteria.getAddress() != null && !criteria.getAddress().trim().isEmpty()) {
	            sqlBuilder.append(" AND address LIKE :address");
	            params.addValue("address", "%" + criteria.getAddress().trim() + "%");
	        }
	    }
	    return namedParameterJdbcTemplate.query(sqlBuilder.toString(), params, memberRowMapper);
	}
	
	@Override
	public List<Member> findMembersByIds(List<Integer> memberIds) throws DataAccessException {
	    if (memberIds == null || memberIds.isEmpty()) {
	        return Collections.emptyList();
	    }
	    String sql = "SELECT * FROM members WHERE memberID IN (:memberIds)";
	    MapSqlParameterSource params = new MapSqlParameterSource("memberIds", memberIds);
	    return namedParameterJdbcTemplate.query(sql, params, memberRowMapper);
	}
	
	private void logMemberChange(Member member) throws DataAccessException {
		String sql = "INSERT INTO members_log (MemberId,Name,Email,PhoneNumber,Gender,Address,LogDate) "
				+ "VALUES (:memberId,:name,:email,:phoneNumber,:gender,:address,CURRENT_TIMESTAMP)";
		
		MapSqlParameterSource params = new MapSqlParameterSource()
				.addValue("memberId", member.getMemberID())
				.addValue("name", member.getName())
				.addValue("email", member.getEmail())
				.addValue("phoneNumber", member.getPhoneNumber())
				.addValue("gender", String.valueOf(member.getGender().getCode()))
				.addValue("address", member.getAddress());
		
		namedParameterJdbcTemplate.update(sql, params);
	}
}