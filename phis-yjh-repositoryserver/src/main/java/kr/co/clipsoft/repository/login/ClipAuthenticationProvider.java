package kr.co.clipsoft.repository.login;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import kr.co.clipsoft.repository.model.ClipUserDto;
import kr.co.clipsoft.repository.service.ClipUserService;

@Component
public class ClipAuthenticationProvider implements AuthenticationProvider {
	private static final Logger logger = LoggerFactory.getLogger(ClipAuthenticationProvider.class);
    
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private ClipUserService clipUserService;
	
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
  
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = (String)authentication.getPrincipal();    
        String password = (String)authentication.getCredentials();
         
        ClipUserDto userDto = new ClipUserDto();
        userDto.setProductId(new Long(1));
        userDto.setUserId(userId);
        userDto.setPassword(password);
        
        ClipUserDto validUserDto = clipUserService.get(userDto); 
        
        boolean matchesPassword = bCryptPasswordEncoder.matches(password, validUserDto.getPassword());
        
        if(matchesPassword){
            List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));

            ClipUserDetails details = new ClipUserDetails(userId, password);
            details.setUserDto(validUserDto);
            
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userId, password, roles);
            result.setDetails(details);
            return result;         
        } else {
            logger.info("사용자 credentials 정보가 틀립니다. 에러가 발생합니다.");
            throw new BadCredentialsException("Bad credentials");
        }
    }
    
    public Authentication directAuthenticate(Authentication authentication) throws AuthenticationException {
    		String userId = (String)authentication.getPrincipal();    
        String password = (String)authentication.getCredentials();
         
        ClipUserDto userDto = new ClipUserDto();
        userDto.setProductId(new Long(1));
        userDto.setUserId(userId);
        userDto.setPassword(password);
        
        ClipUserDto validUserDto = clipUserService.get(userDto);
        if(validUserDto == null) {
        		throw new BadCredentialsException("등록되지 않은 사용자 정보");
        }
        
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));

        ClipUserDetails details = new ClipUserDetails(userId, password);
        details.setUserDto(validUserDto);
        
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(userId, password, roles);
        result.setDetails(details);
        return result;
    }
}
