package com.springboot.project.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.springboot.project.model.Roles;

@Repository
public class RolesRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public List<Roles> getAllRoles() {
		List<Roles> roles = new ArrayList<>();
		try {
			roles = jdbcTemplate.query("select * from role", (ResultSet rs, int rowNum)-> {
						Roles s = new Roles();
						s.setId(rs.getInt(1));
						s.setName(rs.getString(2));
						return s;
					});
		} catch(Exception ex) {}
		return roles;
	}
}
