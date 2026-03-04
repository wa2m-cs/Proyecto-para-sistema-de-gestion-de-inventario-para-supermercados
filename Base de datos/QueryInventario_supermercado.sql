create database inventario_supermercado; 

  

use inventario_supermercado; 

  

create table Categoria( 

id_categoria int primary key identity(1,1), 

nombre_categoria varchar(100) not null 

); 

  

create table Rol( 

id_rol int primary key identity(1,1), 

rol varchar(70) not null 

); 

  

create table Ubicacion( 

id_ubicacion int primary key identity(1,1), 

ubicacion varchar(70) not null 

); 

  

create table Producto( 

id_producto int primary key identity(1,1), 

nombre_producto varchar(150) not null, 

estado varchar(50) not null, 

id_categoria int, 

descripcion varchar(250) not null, 

precio decimal (10,2) not null, 

imagen nvarchar(500) not null, 

activo bit, 

foreign key (id_categoria) references Categoria(id_categoria) 

); 

  

create table Usuario( 

id_usuario int primary key identity(1,1), 

activo bit not null, 

Nombre varchar(100) not null, 

Apellido varchar(100) not null, 

contraseña varchar(50) not null, 

correo varchar(150) not null, 

id_rol int, 

foreign key (id_rol) references Rol(id_rol) 

); 

  

create table Pedido( 

id_pedido int primary key identity(1,1), 

id_usuario int, 

total decimal (4,2) not null, 

fecha_pago datetime not null, 

estado varchar(20) not null, 

fecha_pedido datetime, 

foreign key (id_usuario) references Usuario(id_usuario) 

); 

  

create table Inventario( 

id_inventario int primary key identity(1,1), 

id_ubicacion int, 

id_producto int, 

cantidad int not null, 

fecha_vencimiento date not null, 

foreign key (id_ubicacion) references Ubicacion(id_ubicacion), 

foreign key (id_producto) references Producto(id_producto) 

); 

  

create table DetallePedido( 

id_detalle int primary key identity(1,1), 

id_producto int, 

id_pedido int, 

cantidad int not null, 

subtotal decimal(10,2) not null, 

foreign key (id_producto) references Producto(id_producto), 

foreign key (id_pedido) references Pedido(id_pedido) 

); 

  

create table Asignacion( 

id_asignacion int primary key identity(1,1), 

id_usuario int, 

id_ubicacion int, 

puesto varchar(100) not null, 

fecha_inicio datetime, 

fecha_fin datetime, 

activo bit, 

foreign key (id_usuario) references Usuario(id_usuario), 

foreign key (id_ubicacion) references Ubicacion(id_ubicacion) 

); 