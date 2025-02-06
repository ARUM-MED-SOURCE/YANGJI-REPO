package kr.co.clipsoft.biz.service;

import kr.co.clipsoft.biz.model.NuUserDto;

public interface NuUserService {

	public boolean loginNu(String userId, String password);

	public NuUserDto loginNu2(String userId, String password);

}
