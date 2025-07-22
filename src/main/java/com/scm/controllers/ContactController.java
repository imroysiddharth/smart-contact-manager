package com.scm.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequestMapping("/user/contacts")
public class ContactController {
    
    @Autowired
    private ImageService imageService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    // add contact page: handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();

        // contactForm.setFavorite(true);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method=RequestMethod.POST)
    public String requestMethodName(@Valid @ModelAttribute ContactForm contactForm, BindingResult result , Authentication authentication, HttpSession session){

        //validation 
          if (result.hasErrors()) {

             session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());

            return "user/add_contact";
        }



        //process the form data 
                String username  = Helper.getEmailOfLoggedInUser(authentication);

                //Contact Form ---> Contaact  
                User user = userService.getUserByEmail(username);

            // Process the Pic
            //File Upload Code
                String filename = UUID.randomUUID().toString();
                String fileURL = imageService.uploadImage(contactForm.getContactImage(),filename);

                Contact contact = new Contact();
                contact.setName(contactForm.getName());
                contact.setFavorite(contactForm.isFavorite());
                contact.setEmail(contactForm.getEmail());
                contact.setPhoneNumber(contactForm.getPhoneNumber());
                contact.setAddress(contactForm.getAddress());
                contact.setDescription(contactForm.getDescription());
                contact.setUser(user);
                contact.setLinkedInLink(contactForm.getLinkedInLink());
                contact.setWebsiteLink(contactForm.getWebsiteLink());
                contact.setPicture(fileURL);

            System.out.println(contactForm.toString());


            // Set contact pic url
            //Set the message to display 
            contact.setCloudinaryImagePublicId(filename);
            contactService.save(contact);

               session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());


        return "redirect:/user/contacts/add";
    }
    //


    //View Contact
    @GetMapping
    public String viewContacts(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = ""+AppConstants.PAGE_SIZE) int size, 
        @RequestParam(value = "sortBy", defaultValue = "name") String sortBy, 
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        Model model,Authentication authentication){

        String username = Helper.getEmailOfLoggedInUser(authentication);
        //Load All Contacts
        User user = userService.getUserByEmail(username); 
        Page<Contact> PageContact = contactService.getByUser(user,page,size,sortBy,direction);
        // System.out.println();
        model.addAttribute("PageContact", PageContact);
        model.addAttribute("pagesize", AppConstants.PAGE_SIZE);
        return "user/contacts";
    }
    

    //Search handler
    @GetMapping("/search")
    public String searchHandler(
          @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication
     ){

        // System.out.println("f:"+field);
        // System.out.println("k:"+value);



           var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }

        
        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);





        return "/user/search";
    }
}
