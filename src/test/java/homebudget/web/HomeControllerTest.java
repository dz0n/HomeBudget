package homebudget.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class HomeControllerTest {
	
	@Test
	public void testHomePageUnauthorized() throws Exception {
		HomeBudgetController controller = new HomeBudgetController();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		setUnauthorizedUser();
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.view().name("home"));
	}
	
	@Test
	public void testHomePageauthorized() throws Exception {
		HomeBudgetController controller = new HomeBudgetController();
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		
		setAuthorizedUser();
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.redirectedUrl("/receipt"));
	}
	
	private void setUnauthorizedUser(){
		Collection<GrantedAuthority> grandtedAuthorities = new ArrayList<GrantedAuthority>();
		grandtedAuthorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "abc";
			}
		};
		Authentication auth = new AnonymousAuthenticationToken("anonymousUser", principal, grandtedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
	
	private void setAuthorizedUser(){
		Collection<GrantedAuthority> grandtedAuthorities = new ArrayList<GrantedAuthority>();
		grandtedAuthorities.add(new SimpleGrantedAuthority("ROLE_FULL"));
		User user = new User("user1", "abc", true, false, false, false, grandtedAuthorities);
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, grandtedAuthorities);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}
}
