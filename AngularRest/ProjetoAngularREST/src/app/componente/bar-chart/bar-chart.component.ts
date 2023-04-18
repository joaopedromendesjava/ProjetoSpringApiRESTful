import { Component, OnInit } from '@angular/core';
import { ChartOptions, ChartType, ChartDataSets } from 'chart.js';
import { Label } from 'ng2-charts';
import { UsuarioService } from 'src/app/Service/usuario.service';
import { UserChart } from 'src/app/model/UserChart';

@Component({
  selector: 'app-bar-chart',
  templateUrl: './bar-chart.component.html',
  styleUrls: ['./bar-chart.component.css']
})
export class BarChartComponent implements OnInit {

  barChartData: { data: number[] ; label: string; }[];

  constructor(private userService : UsuarioService) {}
  
  userChart = new UserChart();

  ngOnInit(): void {

    this.userService.carregarGraficoChart().subscribe(data =>{

      this.userChart = data; // userchart = retorno do backend
      
      this.barChartLabels = this.userChart.nome.split(',');    //nomes

      var arraySalario = JSON.parse('[' + this.userChart.salario + ']'); //salario 

      this.barChartData  = [
        { data: arraySalario, label: 'Salário Usuário' }
      ];
    
    });
  }

  barChartOptions: ChartOptions = {
    responsive: true,
  };

  barChartLabels: Label[] = [];
  lineChartType : ChartType = 'bar';
  barChartLegend = true;
  barChartPlugins = [];
  
  
}