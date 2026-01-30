package com.lyon.bank;

import com.lyon.bank.security.config.JwtProperties;
import com.lyon.bank.security.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RsaKeyProperties.class, JwtProperties.class})
public class LyonBankApplication {

  public static void main(String[] args) {
    SpringApplication.run(LyonBankApplication.class, args);
  }

}
