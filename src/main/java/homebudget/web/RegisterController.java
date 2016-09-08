package homebudget.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import homebudget.domain.forms.RegisterUserForm;
import homebudget.domain.forms.RegisterUserForm.Validation;
import homebudget.service.UserService;

@Controller
@RequestMapping(value="/register")
public class RegisterController {
	private UserService userService;
	
	@Autowired
	public RegisterController(UserService userService) {
		this.userService = userService;
	}
	
	@ModelAttribute("user")
	public RegisterUserForm addRegisterUserForm() {
		return new RegisterUserForm();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public String showRegistrationForm(Model model) {
		return "register";
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public String processRegistration(@ModelAttribute("user") @Validated(Validation.class) RegisterUserForm user, Errors errors, Model model) {
		if(errors.hasErrors()) {
			return "register";
		}
		if(isPasswordRepeatedInCorrectly(user)) {
			model.addAttribute("msgInfo", "Hasło różni się od powtórzonego hasła.");
			return "register";
		}
		if(!userService.isNameUnique(user.getUsername())) {
			model.addAttribute("msgInfo", "Nazwa użytkownika jest zajęta.");
			return "register";
		}
		if(!userService.isEmailUnique(user.getEmail())) {
			model.addAttribute("msgInfo", "Adres e-mail jest zajęty.");
			return "register";
		}
		
		userService.save(user);
		return "redirect:/budget";
	}

	private boolean isPasswordRepeatedInCorrectly(RegisterUserForm user) {
		return !user.getPassword().equals(user.getRepeatedPassword());
	}
	
}
