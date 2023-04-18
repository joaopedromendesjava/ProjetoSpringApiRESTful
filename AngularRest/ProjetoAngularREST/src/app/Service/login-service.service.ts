import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppConstants } from '../app-constants';
import { Router } from '@angular/router'
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class LoginServiceService {

  constructor(private http : HttpClient, private router : Router) { }


  recuperar(login : any) {

    let user = new User();
    user.login = login;

    return this.http.post<any>(AppConstants.BaseUrlPath + 'recuperar/' , user).subscribe(data => {

      alert(JSON.parse(JSON.stringify(data)).error);
      console.log(data);

    }, Error =>{
        console.error("Erro ao recuperar login");
        alert('Erro ao recuperar login!');

    });
 }

    login(usuario: { login: string; senha: string; }) {

      return this.http.post<any>(AppConstants.baseLogin , JSON.stringify(usuario)).subscribe(data => {

        //Corpo do retorno HTTP
      var token = (JSON.parse(JSON.stringify(data)).Authorization.split(' ')[1]); //pego token apos Bearer
          localStorage.setItem("token", token); //guardo em memória

       //console.info("token " + localStorage.getItem("token"));
        
        this.router.navigate(['home']);

      }, Error =>{
          console.error("Erro ao fazer login");
          alert('Acesso negado, revise as autenticações ou solicite seu usuário ao admin');

      });
   }

   
}


