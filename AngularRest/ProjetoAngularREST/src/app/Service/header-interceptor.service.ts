import { HttpEvent, HttpHandler, HttpInterceptor, HTTP_INTERCEPTORS,
    HttpRequest, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable, NgModule } from '@angular/core';
import { throwError } from 'rxjs';
import { Observable } from 'rxjs';
import { catchError, tap } from 'rxjs';

@Injectable()

export class HeaderInterceptorService implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    if(localStorage.getItem('token') !== null){

      const token = 'Bearer ' + localStorage.getItem('token');

      const tokenRequest = req.clone({
          headers : req.headers.set('Authorization', token)
      })
    
      return next.handle(tokenRequest).pipe(  
        
        tap((event : HttpEvent<any>) =>{

          if(event instanceof HttpResponse && (event.status === 200 || event.status === 201)){

        }
      }) ,catchError(this.processError));
    
    }else{

      return next.handle(req).pipe(catchError(this.processError));

    }
 
  }

  constructor() { }

    processError(error : HttpErrorResponse){
      
        let errorMessage = 'Erro desconhecido';
        
        if(error.error instanceof ErrorEvent){
            errorMessage = 'Error: ' + error.error.error;
        
        }else{

          if(error.status === 403 ){
            errorMessage = 'Acesso Negado: Faça login novamente.'
          }
          else{
            errorMessage = 'Código ' + error.error.code + '\nMensagem: ' + error.error.error
          }
        }

        window.alert(errorMessage)
      return throwError(errorMessage);
  }
}

@NgModule({
  providers : [{

    provide : HTTP_INTERCEPTORS,
    useClass : HeaderInterceptorService,
    multi : true,
  
  }],
})

export class HttpInterceptorModule {


}