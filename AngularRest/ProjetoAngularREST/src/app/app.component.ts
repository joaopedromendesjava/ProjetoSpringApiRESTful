import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent implements OnInit {
  title = 'ProjetoAngularREST';
  
constructor(private router : Router){

}


  ngOnInit(): void {
    if(localStorage.getItem('token') == null){
      this.router.navigate(['login']);
    }
  }

  public sair() {
      localStorage.clear();
      this.router.navigate(['login']);    
  }

  public esconderBarr() {

    if(localStorage.getItem('token') !== null &&
        localStorage.getItem('token')?.toString().trim() !== null){

          return false; // mostra navbar

    }else{
      
      return true; // esconde navbar
    }

  }


}
