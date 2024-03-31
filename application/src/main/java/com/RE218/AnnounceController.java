package com.RE218;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AnnounceController {

	@GetMapping("/announce")
	public String greetingForm(Model model) {
		model.addAttribute("announce", new Announce());
		return "announce";
	}

	@PostMapping("/announce")
	public String greetingSubmit(@ModelAttribute Announce announce, Model model) {
		model.addAttribute("announce", announce);
		return "result";
	}

}
