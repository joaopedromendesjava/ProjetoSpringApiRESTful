import { formatDate } from '@angular/common';
import { Component, Injectable, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router'; // para o editar funcionar
import { NgbDateParserFormatter , NgbDateStruct, NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';
import { userReport } from 'src/app/model/userReport';
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

    return date ? validarDia(date.day) + this.DELIMITER + validarDia(date.month) 
        + this.DELIMITER + validarDia(date.year) : null;
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

    return date ? validarDia(date.day) + this.DELIMITER + validarDia(date.month) + 
      this.DELIMITER + validarDia(date.year) : null;
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
  templateUrl: './usuario-report.component.html',
  styleUrls: ['./usuario-report.component.css'],
  providers: [{provide: NgbDateParserFormatter, useClass: FormatDate},
        {provide: NgbDateAdapter, useClass : FormatDateAdapter}] 
})
export class UsuarioReportComponent {

  userReport = new userReport();
  
    constructor( 

      private routeActive : ActivatedRoute,
      private userService : UsuarioService ){
  }

  imprimeRelatorioParam(){

    this.userService.downloadPdfRelatorioParam(this.userReport);

    console.log(this.userReport);
  }
}
