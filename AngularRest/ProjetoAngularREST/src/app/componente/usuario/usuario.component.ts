import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from 'src/app/model/user';
import { UsuarioService } from 'src/app/Service/usuario.service';


@Component({
  selector: 'app-usuario',
  templateUrl: './usuario.component.html',
  styleUrls: ['./usuario.component.css']
})
export class UsuarioComponent implements OnInit {

   students : Array<User>;
   nome : string;
   p :number = 1;
   total: number;
  
  constructor(private usuarioService : UsuarioService){

  }

  ngOnInit(){ //quando iniciar a tela faz a requisição no back e atribui para a variável students
       
      this.usuarioService.getStudentList().subscribe(data => {
        this.students = data.content;
        this.total = data.totalElements;

      });
  }

  deleteUsuario(id : number, index : number){

    if(confirm('Deseja realmente excluir o usuário ?')){

    this.usuarioService.deletarUsuario(id).subscribe(data =>{
      this.students.splice(index, 1);
    });

  }
}

  consultarUser(){

    if(this.nome === ''){

      this.usuarioService.getStudentList().subscribe(data => {
        this.students = data.content;
        this.total = data.totalElements;

      });

    }else {

    this.usuarioService.consultarUser(this.nome).subscribe(data =>{
      this.students = data.content;  //atualizo a lista com o valor da busca
      this.total = data.totalElements;
    });
  }
}

  carregarPage(page : number){

    if(this.nome !== ''){

      this.usuarioService.consultarUserPage(this.nome, (page - 1)).subscribe(data =>{
        this.students = data.content;  
        this.total = data.totalElements;
      });
    }
    else {

      this.usuarioService.getStudentListPage(page - 1).subscribe(data => {
        this.students = data.content;
        this.total = data.totalElements;
      
      });
    }
  }

  imprimeRelatorio(){

    this.usuarioService.downloadPdfRelatorio();
  }
}