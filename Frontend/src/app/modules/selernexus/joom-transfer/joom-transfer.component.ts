import { Component, OnInit } from '@angular/core';
import { JoomAuthService } from '../../../core/services/sellernexus/joom-auth.service';
import { JoomTransferService } from '../../../core/services/sellernexus/joom-transfer.service';

@Component({
  selector: 'app-joom-transfer',
  templateUrl: './joom-transfer.component.html',
  styleUrls: ['./joom-transfer.component.scss']
})
export class JoomTransferComponent implements OnInit {
  credentials: any[] = [];
  srcCredentialId: number | null = null;
  tgtCredentialId: number | null = null;
  srcProductId = '';
  message = '';
  loading = false;
  response: any = null;
  errorMsg: string | null = null;

  constructor(private auth: JoomAuthService, private transfer: JoomTransferService) { }

  ngOnInit(): void {
    this.loadCredentials();
  }

  loadCredentials() {
    this.auth.getCredentials().subscribe({
      next: (res: any[]) => {
        this.credentials = res || [];
        if (this.credentials.length >= 2) {
          this.srcCredentialId = this.credentials[0].id;
          this.tgtCredentialId = this.credentials[1].id;
        } else if (this.credentials.length === 1) {
          this.srcCredentialId = this.credentials[0].id;
        }
      },
      error: (err) => { console.error('failed to load creds', err); }
    });
  }

  transferOne() {
    if (!this.srcCredentialId || !this.tgtCredentialId || !this.srcProductId) {
      this.message = 'Please select source, target and enter a product id';
      return;
    }
    this.loading = true;
    this.response = null;
    this.errorMsg = null;
    this.transfer.transferOne(this.srcCredentialId, this.tgtCredentialId, this.srcProductId).subscribe({
      next: (res) => {
        this.response = res;
        this.message = 'Transfer completed';
        this.loading = false;
      },
      error: (err) => {
        console.error('transfer failed', err);
        this.errorMsg = err?.error?.error || err?.error?.message || err.message || 'unknown';
        this.message = 'Transfer failed';
        this.loading = false;
      }
    });
  }

  copyResponse() {
    try {
      const text = this.response ? JSON.stringify(this.response, null, 2) : '';
      navigator.clipboard.writeText(text);
    } catch (e) { }
  }

  copyError() {
    try { navigator.clipboard.writeText(this.errorMsg || ''); } catch(e) {}
  }

}
