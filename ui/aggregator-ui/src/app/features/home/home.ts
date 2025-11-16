import {Component, inject, OnInit, signal} from '@angular/core';
import {Transaction} from '../../model/transaction';
import {HttpClient} from '@angular/common/http';
import {TableModule} from 'primeng/table';
import {FileUpload, FileUploadEvent} from 'primeng/fileupload';
import {Button} from 'primeng/button';

@Component({
  selector: 'app-home',
  imports: [
    TableModule,
    FileUpload,
    Button,
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {

  private http = inject(HttpClient);
  allTransactions = signal<Transaction[]>([]);

  ngOnInit() {
    this.loadData();
  }

  loadData() {


    // Mock data for demonstration
    this.allTransactions.set([
      { id: 1, date: '2024-01-15', description: 'Grocery Store', amount: 85.50, category: 'GROCERIES', source: 'CREDIT_CARD' },
      { id: 2, date: '2024-01-16', description: 'Gas Station', amount: 45.00, category: 'TRANSPORTATION', source: 'DEBIT_CARD' },
      { id: 3, date: '2024-01-17', description: 'Restaurant', amount: 62.30, category: 'DINING', source: 'CREDIT_CARD' },
      { id: 4, date: '2024-01-18', description: 'Electric Bill', amount: 120.00, category: 'UTILITIES', source: 'BANK_TRANSFER' },
      { id: 5, date: '2024-01-19', description: 'Online Shopping', amount: 199.99, category: 'SHOPPING', source: 'CREDIT_CARD' }
    ]);
  }

  onUpload($event: FileUploadEvent) {
    console.log("Hi");
  }
}
