package homebudget.web;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ApplicationExceptionHandler {	
	@ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
	public String IntegrityConstraintViolationException(Model model) {
		model.addAttribute("exceptionMessage", "Nie można usunąć obiektu, jeśli posiada podobiekty (np. nie można usunąć kategorii jeśli posiada podkategorie). Usuń najpierw podobiekty.");
		return "error/error";
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public String accessDeniedException() {
		return "error/error403";
	}
	
	@ExceptionHandler(NullPointerException.class)
	public String nullPointerException() {
		return "error/error404";
	}
	
	@ExceptionHandler(Throwable.class)
	public String exception(Exception e, Model model) {
		return "error/error";
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
	public String exception404() {
		return "error/error404";
	}
}
