import { formatDate } from '@angular/common';
import { Component, Injectable, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router'; // para o editar funcionar
import { NgbDateParserFormatter , NgbDateStruct, NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';
import { Profissao } from 'src/app/model/Profissao';
import { Telefone } from 'src/app/model/telefone';
import { User } from 'src/app/model/user';
import { UsuarioService } from 'src/app/Service/usuario.service';

@Injectable()
export class FormatDateAdapter extends NgbDateAdapter<string>{

  readonly DELIMITER = '/';

  fromModel(value: string | null): NgbDateStruct | null {

    if(value){
      let date = value.split(this.DELIMITER);
      return{
        day : parseInt(date[0], 10),
        month : parseInt(date[1], 10),
        year : parseInt (date[2], 10)
      };
    }
    return null;

  }

  toModel(date: NgbDateStruct | null) : string | null {

    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : null;
  }
}


@Injectable()
export class FormatDate extends NgbDateParserFormatter{

  readonly DELIMITER = '/';

  parse(value : string) : NgbDateStruct  | null { // formatando o json 

    if(value){
      let date = value.split(this.DELIMITER);
        return{
          day : parseInt(date[0], 10),
          month : parseInt(date[1], 10), 
          year : parseInt(date[2], 10),
        };
    }
    return null;
  }

  format(date : NgbDateStruct) : string { //como sera formatado em tela

    return date ? validarDia(date.day) + this.DELIMITER + validarDia(date.month) + 
      this.DELIMITER + validarDia(date.year) : '';
  }

  toModel( date : NgbDateStruct | null) : string | null { //vindo do banco um modelo de dados para tela

    return date ? date.day + this.DELIMITER + date.month + this.DELIMITER + date.year : null;
  }
   
}

function validarDia(valor : any) { 

  if(valor.toString !== '' && parseInt(valor) <= 9){
    return '0' + valor;
 }
  return valor;

 }

@Component({
  selector: 'app-root',
  templateUrl: './usuario-add.component.html',
  styleUrls: ['./usuario-add.component.css'],
  providers: [{provide: NgbDateParserFormatter, useClass: FormatDate},
        {provide: NgbDateAdapter, useClass : FormatDateAdapter}] 
})
export class UsuarioAddComponent implements OnInit {

  usuario = new User();
  telefone = new Telefone();
  profissoes : Array<Profissao>;

  
  constructor( 

    private routeActive : ActivatedRoute,
    private userService : UsuarioService ){
}

  ngOnInit(){

    this.userService.getProfissaoList().subscribe(data => {
      this.profissoes = data;
      console.log(this.profissoes);
    })

   let id = this.routeActive.snapshot.paramMap.get('id');

        if(id != null){

            this.userService.getStudent(id).subscribe(data => {
             this.usuario = data;
             console.log(this.usuario);
          });   
        } 
      }

    salvarUser(){
      
        if(this.usuario.id != null && this.usuario.id.toString().trim() != null){ // se ja existe id ou seja atualizando

          this.userService.uptadeUser(this.usuario).subscribe(data =>{
            this.novo();

            console.info("Usuario atualizado " + data);
          });

        } else {

          this.userService.salvarUser(this.usuario).subscribe(data => {  // se nao salvo um novo user
            this.novo();

            console.info("Usuario salvo: " + data);
          });
        }
  
  }

  removeTelephone(id : any, i : any){

        if(id === null){
          
          this.usuario.telefones.splice(i, 1);
          return;
        }   

        if(id !== null && confirm("Deseja remover?")){

          this.userService.removeTelephone(id).subscribe(data =>{
            this.usuario.telefones.splice(i,1); // remove um elemento (o indice da linha)
            
            console.info("Telefone removido = " + data); 
          })

        } 
  }

  addFone(){

        if(this.usuario.telefones === undefined){
          this.usuario.telefones = new Array<Telefone>();
        
        }

        this.usuario.telefones.push(this.telefone);
         this.telefone = new Telefone();
    }


  novo(){

      this.usuario = new User();
        this.telefone = new Telefone();
  }
}
