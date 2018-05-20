package org.zerhusen.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PassKeyRestService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(path = "/passkey/{pass}", method = RequestMethod.GET)
    public String getPassKey(@PathVariable String pass) {
        BCryptPasswordEncoder BCPE = new BCryptPasswordEncoder();
        String converted = BCPE.encode(pass);
        logger.debug("Converting pass: {} to {}", pass, converted);
        return "Converted: " + converted;
    }

}
