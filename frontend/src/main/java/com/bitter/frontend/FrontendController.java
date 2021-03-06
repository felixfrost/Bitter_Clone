package com.bitter.frontend;

import org.apache.logging.log4j.message.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class FrontendController {

    @GetMapping("/signup")
    public String createUser (Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/logout")
    public String logout (HttpSession session) {
        session.setAttribute("currentUser", null);
        return "redirect:/";
    }

    // login
    @GetMapping("/")
    public String index(Model model, HttpSession session){
        if(session.getAttribute("currentUser")!=null){
            model.addAttribute("newbeet", new Beet());
            return "home";
        }

        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/")
    public String addUser(@ModelAttribute User user, RestTemplate restTemplate){

        User newUser = restTemplate.postForObject("http://localhost:8081/adduserobj",user, User.class);
        System.out.println(newUser.getFirstname());


        return "redirect:/";
    }

    @GetMapping("/user/{username}")
    public String getUser(@PathVariable("username") String username, Model model, RestTemplate restTemplate){
        model.addAttribute("user", restTemplate.getForObject("http://localhost:8081/username/"+username,User.class));
        model.addAttribute("userbeets", restTemplate.getForObject(
                "http://localhost:8081/beet/"+username, ArrayList.class));
        return "userpage";
    }

    @PostMapping("/user")
    public String getUserHandler(@RequestParam("username") String username){

        return "redirect:/user/" + username;
    }

    @PostMapping("/home")
    public String level1post(HttpSession session, @RequestParam String beet){
        List<String> list = (List<String>)session.getAttribute("beetList");
        if (list == null) {
            list = new ArrayList<>();
            session.setAttribute("beetList", list);
        }
        list.add(beet);

        return "home";
    }

    //when user is the correct user
    @PostMapping("/login")
    public String login (@ModelAttribute LoginForm loginForm, RestTemplate restTemplate, HttpSession session){
        boolean result = (restTemplate.postForObject("http://localhost:8081/validate",loginForm,Boolean.class));
        System.out.println(result);
        if(result){
            session.setAttribute("currentUser", restTemplate.getForObject("http://localhost:8081/username/"+loginForm.getUsername(),User.class));
            ArrayList<Beet> beets = restTemplate.getForObject(
                    "http://localhost:8081/beet/"+((User)session.getAttribute("currentUser")).getUsername(),
                    ArrayList.class);
            Collections.reverse(beets);
            session.setAttribute("beets", beets);
        }
        return "redirect:/";
    }

    @PostMapping("/beets/create")
    public String createBeet (@ModelAttribute Beet beet, RestTemplate restTemplate, HttpSession session){
        beet.setCreatedByUsername(((User)session.getAttribute("currentUser")).getUsername());
        Beet newBeet = restTemplate.postForObject("http://localhost:8081/beet", beet, Beet.class);
        ArrayList<Beet> beets = restTemplate.getForObject(
                "http://localhost:8081/beet/"+((User)session.getAttribute("currentUser")).getUsername(),
                ArrayList.class);
        Collections.reverse(beets);
        session.setAttribute("beets", beets);

        return "redirect:/";
    }

    @GetMapping("/beets")
    public String getBeets(RestTemplate restTemplate){
        restTemplate.getForObject("http://localhost:8081/beet", ArrayList.class);
        return "beets";
    }

    @GetMapping("/beets/{username}")
    public String getBeetsByUser(@PathVariable("username") String username, RestTemplate restTemplate){
        restTemplate.getForObject("http://localhost:8081/beet/"+ username, ArrayList.class);
        return "beets";
    }

    @GetMapping("/beets/get/{id}")
    public String getBeetsById(@PathVariable("id") long id, RestTemplate restTemplate){
        restTemplate.getForObject("http://localhost:8081/beet/"+id,Beet.class);
        return "beets";
    }

    @PutMapping("/beets/edit/{id}")
    public String editBeet(@PathVariable("id") long id, @RequestParam("message") String message, RestTemplate restTemplate,HttpSession session){

        if(((User)session.getAttribute("currentUser")).getUsername().equals
                (restTemplate.getForObject("http://localhost:8081/beet/"+ id, Beet.class)
                        .getCreatedByUsername()))
        {
            restTemplate.put("http://localhost:8081/beet/"+id, message, Beet.class);
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PutMapping("/editbeet")
    public String editBeet(@ModelAttribute Beet beet, RestTemplate restTemplate,HttpSession session){
        if(((User)session.getAttribute("currentuser")).getUsername().equals
                (restTemplate.getForObject("http://localhost:8081/beet/"+ beet.getId(), Beet.class)
                        .getCreatedByUsername()))
        {
            restTemplate.put("http://localhost:8081/editbeet", beet);
            ArrayList<Beet> beets = restTemplate.getForObject(
                    "http://localhost:8081/beet/"+((User)session.getAttribute("currentUser")).getUsername(),
                    ArrayList.class);
            return "redirect:/";
        }
        return "redirect:/";
    }

    @PostMapping("/beets/delete/{id}")
    public String deleteBeet(@PathVariable("id") long id, RestTemplate restTemplate, HttpSession session){
        if(((User)session.getAttribute("currentUser")).getUsername().equals
                (restTemplate.getForObject("http://localhost:8081/beetid/"+ id, Beet.class)
                        .getCreatedByUsername())) {
            restTemplate.delete("http://localhost:8081/beet/" + id, Beet.class);
            //here we make a new updated list with the existing beets minus the deleted one
            ArrayList<Beet> beets = restTemplate.getForObject(
                    "http://localhost:8081/beet/"+((User)session.getAttribute("currentUser")).getUsername(),
                    ArrayList.class);
            Collections.reverse(beets);
            session.setAttribute("beets", beets);
            return "redirect:/";
        }
        return "redirect:/";
    }
    /*
    @PostMapping("/login")
    public String login (@Valid User user, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "signup";
        }

        return "login";

    }

    // posting when logged in
    @GetMapping("/home")
    public String level1(){
        return "tryHome";
    }
    */


    @GetMapping("/message")
    public String getmsg(RestTemplate restTemplate, Model model){
         List<Message> msg  = restTemplate.getForObject("http://localhost:8081/message", ArrayList.class);
         model.addAttribute("msg", msg);
        return "message";
    }

    @GetMapping("/message/{username}")
    public String getmsgforuser(RestTemplate restTemplate, Model model, @PathVariable String username){
        List<Message> msg  = restTemplate.getForObject("http://localhost:8081/message/" + username, ArrayList.class);
        model.addAttribute("msg", msg);
        return "message";
    }



    @GetMapping("/message1")
    public String getMessages(RestTemplate restTemplate, Model model, HttpSession session){
        List<Message> msg  = restTemplate.getForObject("http://localhost:8081/message/" + ((User)session.getAttribute("currentUser")).getUsername(), ArrayList.class);
        model.addAttribute("msg", msg);
        return "message";
    }
}


