package com.ecanteen.ecanteen.utils;

import com.ecanteen.ecanteen.entities.User;

import java.sql.SQLException;

public interface LoginService {
    User login(String username, String password) throws SQLException, ClassNotFoundException;
}
