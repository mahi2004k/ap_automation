package com.ap_automation.service;

import com.ap_automation.entity.RefreshToken;
import com.ap_automation.entity.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken verifyExpiration(RefreshToken token);

    RefreshToken findByToken(String token);
}
