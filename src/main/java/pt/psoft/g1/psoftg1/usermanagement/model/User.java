/*
 * Copyright (c) 2022-2024 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package pt.psoft.g1.psoftg1.usermanagement.model;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pt.psoft.g1.psoftg1.shared.model.Name;

import lombok.Getter;
import lombok.Setter;


public class User implements UserDetails {

	@Serial
	private static final long serialVersionUID = 1L;

	@Setter
	@Getter
	private Long id;

	@Setter
	@Getter
	private Long version;

	@Setter
	@Getter
	private LocalDateTime createdAt;

	@Setter
	@Getter
	private LocalDateTime modifiedAt;

	@Setter
	@Getter
	private String createdBy;

	@Setter
	@Getter
	private String modifiedBy;

	@Setter
	@Getter
	private boolean enabled = true;

	@Setter
	@Getter
	@NotNull
	@NotBlank
	private String username;

	@Getter
	@NotNull
	@NotBlank
	private String password;

	@Getter
	private Name name;

	@Getter
	private final Set<Role> authorities = new HashSet<>();

	public User() {
		// for ORM only
	}

	/**
	 *
	 * @param username
	 * @param password
	 */
	public User(final String username, final String password) {
		this.username = username;
		setPassword(password);
	}

	/**
	 * factory method. since mapstruct does not handle protected/private setters
	 * neither more than one public constructor, we use these factory methods for
	 * helper creation scenarios
	 *
	 * @param username
	 * @param password
	 * @param name
	 * @return
	 */
	public static User newUser(final String username, final String password, final String name) {
		final var u = new User(username, password);
		u.setName(name);
		return u;
	}

	/**
	 * factory method. since mapstruct does not handle protected/private setters
	 * neither more than one public constructor, we use these factory methods for
	 * helper creation scenarios
	 *
	 * @param username
	 * @param password
	 * @param name
	 * @param role
	 * @return
	 */
	public static User newUser(final String username, final String password, final String name, final String role) {
		final var u = new User(username, password);
		u.setName(name);
		u.addAuthority(new Role(role));
		return u;
	}

	public void setPassword(final String password) {
		Password passwordCheck = new Password(password);
		final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		this.password = passwordEncoder.encode(password);
	}

    public void addAuthority(final Role r) {
		authorities.add(r);
	}

	@Override
	public boolean isAccountNonExpired() {
		return isEnabled();
	}

	@Override
	public boolean isAccountNonLocked() {
		return isEnabled();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return isEnabled();
	}

	public void setName(String name){
		this.name = new Name(name);
	}
}