// src/main/java/com/example/workmanagement/controller/AuthController.java
package com.example.workmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // For handling request parameters
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        // You can add an empty User object to the model if using Thymeleaf forms binding
        // model.addAttribute("user", new User());
        return "signup"; // refers to src/main/resources/templates/signup.html
    }

    // This method will handle the form submission from signup.html
    // For now, it's just a placeholder. You'll add logic for user registration here.
    @PostMapping("/signup")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // Basic validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match!");
            return "signup"; // Stay on signup page with error
        }

        // TODO: Add actual user registration logic here
        // 1. Check if email/phone already exists in the database.
        // 2. Hash the password before saving (VERY IMPORTANT!).
        // 3. Save the new user to the database using your UserRepository.
        // 4. Handle success or failure.

        System.out.println("Registering user: " + email); // For debugging
        // For now, redirect to sign-in page on success
        return "redirect:/signin?registrationSuccess=true";
    }

    @GetMapping("/signin")
    public String showSignInForm() {
        return "signin"; // refers to src/main/resources/templates/signin.html
    }

    // Spring Security will handle the POST /login automatically.
    // You generally don't need a @PostMapping("/login") here unless you're customizing Spring Security heavily.
    // Handles the sign-in form submission (MOCK)
    @PostMapping("/login") // This URL should match th:action in signin.html
    public String processMockLogin(
            @RequestParam String username, // Get username from form field 'name="username"'
            @RequestParam String password, // Get password from form field 'name="password"'
            RedirectAttributes redirectAttributes) {

        // --- MOCK AUTHENTICATION ---
        // For demonstration, let's use a hardcoded user
        String mockUsername = "bao@gmail.com";
        String mockPassword = "123";

        if (username.equals(mockUsername) && password.equals(mockPassword)) {
            // Mock successful login
            System.out.println("Mock login successful for user: " + username);
            // In a real app, you'd set up Spring Security context or issue a JWT here.
            return "redirect:/dashboard"; // Redirect to dashboard on success
        } else {
            // Mock failed login
            System.out.println("Mock login failed for user: " + username);
            redirectAttributes.addFlashAttribute("error", "Invalid credentials"); // Add error message for redirect
            return "redirect:/signin?error"; // Redirect back to signin with an error parameter
        }
    }

    // Mock Logout (though Spring Security handles this, we'll keep a basic placeholder)
    @PostMapping("/logout")
    public String mockLogout() {
        System.out.println("Mock logout performed.");
        return "redirect:/signin?logout"; // Redirect to signin with logout message
    }
}