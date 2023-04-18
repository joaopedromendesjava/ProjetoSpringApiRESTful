import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginServiceService } from '../Service/login-service.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})

export class LoginComponent implements OnInit {

  usuario = { 
      
    login: '',
    senha: ''
  };
    

constructor(private loginService: LoginServiceService, private router : Router){}

  public login() {

    this.loginService.login(this.usuario);
  }

  public recuperar(){

    this.loginService.recuperar(this.usuario.login);
  }



  ngOnInit(){

    if(localStorage.getItem('token') !== null &&
       localStorage.getItem('token')?.toString().trim() !== null){

        // this.router.navigate(['home']);
    }
    
  }

  displaySweetAlert() {
    Swal.fire({
      title: 'Sucesso!',
      text: 'Do you want to continue',
      icon: 'success',
      confirmButtonText: 'Cool'
    })
  }

}
