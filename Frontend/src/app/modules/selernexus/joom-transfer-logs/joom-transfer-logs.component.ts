import { Component, OnInit } from '@angular/core';
import { JoomTransferLogsService, TransferLog } from '../../../core/services/sellernexus/joom-transfer-logs.service';

@Component({
  selector: 'app-joom-transfer-logs',
  templateUrl: './joom-transfer-logs.component.html',
  styleUrls: ['./joom-transfer-logs.component.scss']
})
export class JoomTransferLogsComponent implements OnInit {
  logs: TransferLog[] = [];
  filteredLogs: TransferLog[] = [];
  loading = false;
  expandedLogId: number | null = null;
  statusFilter = 'ALL';

  constructor(private logsService: JoomTransferLogsService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs() {
    this.loading = true;
    this.logsService.getLogs().subscribe({
      next: (logs) => {
        this.logs = logs;
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load transfer logs', err);
        this.loading = false;
      }
    });
  }

  applyFilter() {
    if (this.statusFilter === 'ALL') {
      this.filteredLogs = this.logs;
    } else {
      this.filteredLogs = this.logs.filter(log => log.status === this.statusFilter);
    }
  }

  toggleExpand(logId: number) {
    this.expandedLogId = this.expandedLogId === logId ? null : logId;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'status-success';
      case 'FAILED': return 'status-failed';
      case 'PENDING': return 'status-pending';
      default: return 'status-unknown';
    }
  }

  formatDuration(ms?: number): string {
    if (!ms) return 'N/A';
    if (ms < 1000) return `${ms}ms`;
    return `${(ms / 1000).toFixed(2)}s`;
  }

  formatDate(dateStr?: string): string {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleString();
  }

  copyToClipboard(text: string) {
    try {
      navigator.clipboard.writeText(text);
    } catch (e) {
      console.error('Failed to copy', e);
    }
  }

  parseJson(jsonStr?: string): any {
    if (!jsonStr) return null;
    try {
      return JSON.parse(jsonStr);
    } catch {
      return jsonStr;
    }
  }
}
