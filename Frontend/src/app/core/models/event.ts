export interface Event {
    type: EventType;
    message: string;
    publisherId: number;
    publisherName: string;
    published: Date;
  }
  
  export enum EventType {
    ADD = 'add',
    UPDATE = 'update',
    DELETE = 'delete'
  }