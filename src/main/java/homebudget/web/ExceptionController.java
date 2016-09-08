package homebudget.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ExceptionController {
	@RequestMapping("/404")
	public String notFoundError() {
		return "error/error404";
	}
	
	@RequestMapping("/500")
	public String generalError() {
		return "error/error500";
	}
}
