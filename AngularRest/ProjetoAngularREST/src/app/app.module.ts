import { NgModule, ModuleWithProviders } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AppComponent } from './app.component'; //Para fazer as requisições Ajax (Post,Get...)
import { HttpClientModule } from '@angular/common/http'; 
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { HttpInterceptorModule } from './Service/header-interceptor.service';
import { UsuarioComponent } from './componente/usuario/usuario.component';
import { UsuarioAddComponent } from './componente/usuario-add/usuario-add.component';
import { RouterGuardGuard } from './Service/router-guard.guard';
import { NgxMaskModule, IConfig } from 'ngx-mask';
import { NgxPaginationModule } from 'ngx-pagination';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxCurrencyModule } from 'ngx-currency';
import { UsuarioReportComponent } from './componente/usuario-report/usuario-report.component';
import { ChartsModule } from 'ng2-charts';
import { BarChartComponent } from './componente/bar-chart/bar-chart.component';
import Swal from 'sweetalert2'

export const appRouters: Routes = [
  
  {path : 'home', component : HomeComponent, canActivate : [RouterGuardGuard]},
  {path : 'login', component : LoginComponent},
  {path : '', component : LoginComponent},
  {path : 'userList', component : UsuarioComponent, canActivate : [RouterGuardGuard]},
  {path : 'usuarioAdd', component : UsuarioAddComponent, canActivate : [RouterGuardGuard]}, //Button Novo
  {path : 'usuarioAdd/:id', component : UsuarioAddComponent, canActivate : [RouterGuardGuard]}, //Button Editar + id
  {path : 'userReport', component : UsuarioReportComponent, canActivate : [RouterGuardGuard]},
  {path : 'chart', component : BarChartComponent, canActivate : [RouterGuardGuard]}

];

export const routes : ModuleWithProviders<any> = RouterModule.forRoot(appRouters); 
export const optionMask : Partial<null|IConfig> | (() => Partial<IConfig>) = {};

@NgModule({
  declarations: [
   
    AppComponent,
        LoginComponent,
        UsuarioComponent,
        UsuarioAddComponent,
        UsuarioReportComponent,
        BarChartComponent
      ],

  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    routes, 
    HttpInterceptorModule,
    NgxMaskModule.forRoot(optionMask),
    NgxPaginationModule,
    NgbModule,
    NgxCurrencyModule,
    ChartsModule,
  
  ],
  providers: [],
  bootstrap: [AppComponent]
})

export class AppModule { }
