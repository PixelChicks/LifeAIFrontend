package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ChatClient;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.PublicUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class MenuController {

    private final ChatClient chatClient;

    @GetMapping
    public String redirectHome(HttpSession session, Model model) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        model.addAttribute("dailyReminder", chatClient.receiveDailyReminder());
        return "menu/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        model.addAttribute("dailyReminder", chatClient.receiveDailyReminder());
        PublicUserDTO user = (PublicUserDTO) session.getAttribute("AUTH_USER");

        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        model.addAttribute("activeTab", "home");
        if (user != null) {
            model.addAttribute("userName", user.getFirstName());
        }
        return "menu/home";
    }

    @GetMapping("/info")
    public String info(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }
        return "menu/info";
    }

    @GetMapping("/information")
    public String information(HttpSession session, Model model) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        model.addAttribute("activeTab", "info");
        return "menu/information";
    }

    @GetMapping("/organPreservingSurgery")
    public String organPreservingSurgery(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/organPreservingSurgery";
    }

    @GetMapping("/menopause")
    public String menopause(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/menopause";
    }

    @GetMapping("/recipes")
    public String recipes(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/recipes";
    }

    @GetMapping("/exercises")
    public String exercises(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises";
    }

    @GetMapping("/stickExercises")
    public String stickExercises(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/stickExercises";
    }

    @GetMapping("/spadeStretching")
    public String spadeStretching(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/spadeStretching";
    }

    @GetMapping("/shoulderBlades")
    public String shoulderBlades(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/shoulderBlades";
    }

    @GetMapping("/tiltSide")
    public String tiltSide(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/tiltSide";
    }

    @GetMapping("/chestStretch")
    public String chestStretch(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/chestStretch";
    }

    @GetMapping("/shoulderStretch")
    public String shoulderStretch(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/shoulderStretch";
    }

    @GetMapping("/moreInfoExercises")
    public String moreInfoExercises(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/exercises/moreInfo";
    }

    @GetMapping("/limfedem")
    public String limfedem(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/limfedem";
    }

    @GetMapping("/alternativeMedicine")
    public String alternativeMedicine(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/alternativeMedicine";
    }

    @GetMapping("/diagnosisUploadFile")
    public String understandingDiagnosisUploadFile(HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        return "menu/understandingDiagnosisUploadFile";
    }
}
