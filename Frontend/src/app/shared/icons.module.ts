import { NgModule } from '@angular/core';
import { FeatherModule } from 'angular-feather';
import {Package, Monitor, BarChart,X,Plus,Check,CornerUpLeft,Trash2,UserX, Bell, Heart, LogOut, MessageSquare, Search, Settings, Terminal, User, UserPlus,Edit, Users, ShoppingCart, Tag,Shield } from 'angular-feather/icons';

const icons = {
  Users, 
  User,
  UserPlus,
  Search,
  Monitor,
  Terminal,
  Bell,
  MessageSquare,
  Heart,
  BarChart, 
  Settings, 
  LogOut,
  Tag,
  ShoppingCart,
  Shield,Package,Edit,Trash2,UserX,X,Plus,Check,CornerUpLeft
};

@NgModule({
  imports: [
    FeatherModule.pick(icons)
  ],
  exports: [
    FeatherModule
  ]
})
export class IconsModule { }