import {ElementRef} from '@angular/core';

declare var M: { toast: (arg0: { html: string; classes: string; }) => void; updateTextFields: () => void; Modal: { init: (arg0: any, arg1: { dismissible: boolean; }) => MaterialInstance; }; Datepicker: { init: (arg0: any, arg1: { format: string; showClearBtn: boolean; onClose: () => void; }) => Datepicker; }; FormSelect: { init: (arg0: any) => MaterialInstance; }; Tabs: { init: (arg0: any) => Tabs; }; Collapsible: { init: (arg0: any) => void; }; Chips: { init: (arg0: any, arg1: { placeholder: string; secondaryPlaceholder: string; data: { tag: string; }[]; autocompleteOptions: { data: {}; minLength: number; }; hasAutocomplete: boolean; }) => void; }; };

export interface MaterialInstance {
  open?(): void;
  close?(): void;
  destroy?(): void;
}

export interface Datepicker extends MaterialInstance {
  date?: Date;
}

export interface Tabs extends MaterialInstance {
  index: number;
  updateTabIndicator(): void;
  select(el: string): void;
}

export class MaterialService {
  static toast(message: string) {
    M.toast({
      html: message,
      classes: 'rounded'
    });
  }

  static error(message: string) {
    M.toast({html: message, classes: 'rounded red-text'});
  }

  static updateInputs() {
    M.updateTextFields();
  }

  static initModal(ref: ElementRef): MaterialInstance {
    return M.Modal.init(ref.nativeElement, {
      dismissible: false
    });
  }

  static initDatepicker(ref: ElementRef, onClose: () => void): Datepicker {
    return M.Datepicker.init(ref.nativeElement, {
      format: 'dd.mm.yyyy',
      showClearBtn: true,
      onClose
    });
  }

  static initSelect(ref: ElementRef): MaterialInstance {
    return M.FormSelect.init(ref.nativeElement);
  }

  static initTabs(ref: ElementRef): Tabs {
    return M.Tabs.init(ref.nativeElement);
  }

  static initCollapsible(ref: ElementRef) {
    M.Collapsible.init(ref.nativeElement);
  }

  static chips(
    ref: ElementRef,
    data: string[],
    placeholder: string,
    secondaryPlaceholder: string
  ): void {
    const autocompleteData: { [key: string]: null } = {};
    data.forEach((el) => {
      autocompleteData[el] = null;
    });
  
    M.Chips.init(ref.nativeElement, {
      placeholder,
      secondaryPlaceholder,
      data: data.map((el) => ({ tag: el })),
      autocompleteOptions: {
        data: autocompleteData,
        minLength: 1
      },
      hasAutocomplete: true
    });
  }
  
}
