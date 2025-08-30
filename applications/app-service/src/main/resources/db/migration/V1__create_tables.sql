CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tipo_prestamo (
    id_tipo_prestamo VARCHAR(50) PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    monto_minimo DECIMAL(10, 2) NOT NULL,
    monto_maximo DECIMAL(10, 2) NOT NULL,
    tasa_interes DECIMAL(5, 2) NOT NULL,
    validacion_automatica BOOLEAN NOT NULL
);

CREATE TABLE solicitud (
  id_solicitud VARCHAR(50) PRIMARY KEY DEFAULT gen_random_uuid(),
  monto DECIMAL(10, 2) NOT NULL,
  plazo INT NOT NULL,
  email VARCHAR(150) NOT NULL,
  id_estado INT NOT NULL,
  id_tipo_prestamo VARCHAR(50) NOT NULL,
  FOREIGN KEY (id_tipo_prestamo) REFERENCES tipo_prestamo(id_tipo_prestamo)
);