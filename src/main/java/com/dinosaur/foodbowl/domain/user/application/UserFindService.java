package com.dinosaur.foodbowl.domain.user.application;

import static com.dinosaur.foodbowl.global.error.ErrorCode.USER_NOT_FOUND;

import com.dinosaur.foodbowl.domain.user.dao.UserRepository;
import com.dinosaur.foodbowl.domain.user.entity.User;
import com.dinosaur.foodbowl.global.error.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserFindService {

    private final UserRepository memberRepository;

    public User findById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(id, "userId", USER_NOT_FOUND));
    }

    public User findByLoginId(final String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(loginId, "loginId", USER_NOT_FOUND));
    }
}
