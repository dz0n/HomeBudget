package homebudget.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import homebudget.domain.User;
import homebudget.domain.forms.RegisterUserForm;
import homebudget.service.UserService;

public class RegisterControllerTest {
	private MockMvc mockMvc;
	private UserService mockRepository;
	
	@Before
	public void setUp() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/templates/");
		resolver.setSuffix(".html");
		
		mockRepository = Mockito.mock(UserService.class);
		
		RegisterController controller = new RegisterController(mockRepository);
		mockMvc = MockMvcBuilders.standaloneSetup(controller)
				.setViewResolvers(resolver)
				.build();
	}
	
	@Test
	public void shouldshowRegistrationForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/register")).andExpect(MockMvcResultMatchers.view().name("register"));
	}
	
	@Test
	public void shouldProcessRegistration() throws Exception {
		RegisterUserForm unsaved = new RegisterUserForm("Jan Kowalski", "123Hasło", "123Hasło", "a@a.pl");
		User saved = new User(15L, "Jan Kowalski");
		Mockito.when(mockRepository.save(unsaved)).thenReturn(saved);
		Mockito.when(mockRepository.isEmailUnique("a@a.pl")).thenReturn(true);
		Mockito.when(mockRepository.isNameUnique("Jan Kowalski")).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/register")
				.param("username", "Jan Kowalski")
				.param("password", "123Hasło")
				.param("repeatedPassword", "123Hasło")
				.param("email", "a@a.pl"))
				.andExpect(MockMvcResultMatchers.redirectedUrl("/budget"));
		Mockito.verify(mockRepository, Mockito.atLeastOnce()).save(unsaved);
	}
	
	@Test
	public void shouldProcessRegistrationWhenNameNotUnique() throws Exception {
		RegisterUserForm unsaved = new RegisterUserForm("Jan Kowalski", "123Hasło", "a@a.pl");
		User saved = new User(15L, "Jan Kowalski");
		Mockito.when(mockRepository.save(unsaved)).thenReturn(saved);
		Mockito.when(mockRepository.isEmailUnique("a@a.pl")).thenReturn(true);
		Mockito.when(mockRepository.isNameUnique("Jan Kowalski")).thenReturn(false);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/register")
				.param("username", "Jan Kowalski")
				.param("password", "123Hasło")
				.param("email", "a@a.pl"))
				.andExpect(MockMvcResultMatchers.view().name("register"));
		Mockito.verify(mockRepository, Mockito.never()).save(unsaved);
	}
	
	@Test
	public void shouldProcessRegistrationWhenEmailNotUnique() throws Exception {
		RegisterUserForm unsaved = new RegisterUserForm("Jan Kowalski", "123Hasło", "a@a.pl");
		User saved = new User(15L, "Jan Kowalski");
		Mockito.when(mockRepository.save(unsaved)).thenReturn(saved);
		Mockito.when(mockRepository.isEmailUnique("a@a.pl")).thenReturn(false);
		Mockito.when(mockRepository.isNameUnique("Jan Kowalski")).thenReturn(true);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/register")
				.param("username", "Jan Kowalski")
				.param("password", "123Hasło")
				.param("email", "a@a.pl"))
				.andExpect(MockMvcResultMatchers.view().name("register"));
		Mockito.verify(mockRepository, Mockito.never()).save(unsaved);
	}
	
}
