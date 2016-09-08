package homebudget.web;

import java.security.Principal;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping({"/"})
public class HomeBudgetController {
	
	@RequestMapping(method=RequestMethod.GET)
	public String home(Principal principal) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof AnonymousAuthenticationToken) {
			return "home";
		} else {
			return "redirect:/receipt";
		}
	}
	
	@RequestMapping(value = {"home", "homepage"}, method=RequestMethod.GET)
	public String homepage() {
		return "home";
	}
}
