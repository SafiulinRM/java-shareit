package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {
	private final UserStorage userStorage;
	private final LocalDate birthday = LocalDate.of(1996, 1, 1);
	private final String email = "qwerty@gmail.com";
	private final String email2 = "update@gmail.com";
	private final String email3 = "test@gmail.com";
	private final String login = "qwerty";
	private final String name = "qwerty123";
	private final String description = "qwertyqwertyqwerty";
	private final int duration = 1000;

	@Test
	void contextLoads() {
	}

	@Test
	public void testUpdateUser() {
		UserDto testUserDto = new UserDto();
		testUserDto.setName(name);
		testUserDto.setEmail(email);
		userStorage.addUser(testUserDto);
		UserDto updateUserDto = new UserDto();
		updateUserDto.setEmail("Petuh@ya.ru");
		updateUserDto.setId(1L);
		UserDto newUserDto = userStorage.updateUser(updateUserDto);
		assertEquals(newUserDto, updateUserDto, "Юзер не обновился!");

	}
}
