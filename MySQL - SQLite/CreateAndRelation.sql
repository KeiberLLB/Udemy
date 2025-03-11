CREATE TABLE Tipos (id INT not null, Nombre VARCHAR(45) not null unique, PRIMARY KEY (id));
CREATE TABLE categorias (idCategoria INT PRIMARY KEY not null, Categoria varchar(10));
create table Especies (
	idEspecies int not null,
    Numero int not null,
    Nombre varchar(45) not null,
    Variante varchar(20) not null,
    FrontSprite varchar(45),
    BackSprite varchar(45),
    CantTipos tinyint(1),
    Tipo1 int not null,
    Tipo2 int,
    PS smallint(3) not null,
    Ataque smallint(3) not null,
    Defensa smallint(3) not null,
    AtaqueSp smallint(3) not null,
    DefensaSp smallint(3) not null,
    Velocidad smallint(3) not null,
    Promedio decimal not null,
    Desviacion decimal not null,
    Suma smallint not null,
    ExpBase smallint(3) not null,
    PS_EV tinyint(1) not null,
    Ataque_EV tinyint(1) not null,
    Defensa_EV tinyint(1) not null,
    AtaqueSp_EV tinyint(1) not null,
    DefensaSp_EV tinyint(1) not null,
    Velocidad_EV tinyint(1) not null,
    primary key(idEspecies),
    foreign key (Tipo1) references Tipos(id),
    foreign key (Tipo2) references Tipos(id));

alter table evolucionpornivel rename column nivel to Nivel;

create table if not EXISTS EvolucionPorNivel (
	idEvolucionPorNivel INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null, nivel TINYINT(3), PRIMARY KEY (idEvolucionPorNivel),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies));
    
    create table if not EXISTS EvolucionPorPiedra (
	idEvolucionPorPiedra INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null, Piedra INT not null, PRIMARY KEY (idEvolucionPorPiedra),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies),
    FOREIGN KEY (Piedra) REFERENCES Items(idItems));
    
create table if not EXISTS EvolucionPorIntercambio (
	idEvolucionPorIntercambio INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null, PRIMARY KEY (idEvolucionPorIntercambio),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies));
    
create table if not EXISTS EvolucionPorIntercambioE (
	idEvolucionPorIntercambioE INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null, Objeto int not null, PRIMARY KEY (idEvolucionPorIntercambioE),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies),
    FOREIGN KEY (Objeto) REFERENCES Items(idItems));
    
create table if not EXISTS EvolucionPorAmistad (
	idEvolucionPorAmistad INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null,Amistad TINYINT(3),Nivel TINYINT(3), PRIMARY KEY (idEvolucionPorAmistad),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies));
    
create table if not EXISTS EvolucionPorAmistadM (
	idEvolucionPorAmistadM INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null, Amistad TINYINT(3),Movimiento INT, PRIMARY KEY (idEvolucionPorAmistadM),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies),
    FOREIGN KEY (Movimiento) REFERENCES movimientos(idmovimientos));
    
create table if not EXISTS EvolucionPorObjetoEquipado (
	idEvolucionPorObjetoEquipado INT NOT NULL, Preevolucion INT not null,
    Evolucion int not null,Objeto int not null, PRIMARY KEY (idEvolucionPorObjetoEquipado),
    foreign key (Preevolucion) REFERENCES Especies(idEspecies),
    FOREIGN KEY (Evolucion) REFERENCES especies(idEspecies),
    FOREIGN KEY (Objeto) REFERENCES Items(idItems));