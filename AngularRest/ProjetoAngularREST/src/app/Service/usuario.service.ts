import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, take } from 'rxjs';
import { AppConstants } from '../app-constants';
import { userReport } from '../model/userReport';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  constructor(private http: HttpClient) {

  }

    getStudentList() : Observable<any>{
      
      return this.http.get<any>(AppConstants.baseUrl);
    }

    deletarUsuario(id: number) : Observable<any> {
      
      return this.http.delete(AppConstants.baseUrl + id, {responseType : 'text'});
    }

    consultarUser (nome : string) : Observable<any>{
    
      return this.http.get(AppConstants.baseUrl + "usuarioPorNome/" + nome)
    }

    getStudent(id : any) : Observable<any> {
      
      return this.http.get<any>(AppConstants.baseUrl + id).pipe(take(1));
    }

    salvarUser(user : any) : Observable<any>{
      
      return this.http.post<any>(AppConstants.baseUrl, user)
    }

    uptadeUser(user : any) : Observable<any>{

      return this.http.put<any>(AppConstants.baseUrl, user)
    }

    removeTelephone (id : number) : Observable<any>{

      return this.http.delete(AppConstants.baseUrl + "removeTelephone/" + id, {responseType: 'text'});
    }

    getStudentListPage(page : number) : Observable<any>{
      
      return this.http.get<any>(AppConstants.baseUrl + "pagination/" + page);
    }

    consultarUserPage (nome : string, page : number) : Observable<any>{
    
      return this.http.get(AppConstants.baseUrl + "usuarioPorNome/" + nome + "/page/" + page);
    }

    getProfissaoList() : Observable<any>{

      return this.http.get<any>(AppConstants.BaseUrlPath + "profissao/");
    }

    downloadPdfRelatorio() {

      return this.http.get(AppConstants.baseUrl + "relatorio", {responseType : 'text'}).subscribe(data =>{
      
        var iframe = document.querySelector('iframe');

        if(iframe !== null){
           iframe.src = data;
      
          }
      });
    }

    downloadPdfRelatorioParam(userreport : userReport ) {

      return this.http.post(AppConstants.baseUrl + "relatorio/", userreport, 
          {responseType : 'text'}).subscribe(data =>{
      
        var iframe = document.querySelector('iframe');
          
        if(iframe !== null){
           
            iframe.src = data;
          }
      });
    }

    carregarGraficoChart() : Observable<any> {

      return this.http.get(AppConstants.baseUrl + 'grafico')
    }

    userAuthentication() : any{

      if(localStorage.getItem('token') !== null &&
         localStorage.getItem('token')?.toString().trim() !== null){
        
        return true;
      
      } else{

          false;
      }
    }

}
