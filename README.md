# Monolith to Microservices Practice

## 📦 API - Registro de Ventas

Este endpoint permite registrar una venta en el sistema, descontar del stock y registrar en contabilidad desde distintos microservicios.

## 🧪 Pruebas

Puedes utilizar el archivo [test.http](test.http) para probar los endpoints de la API. Este archivo contiene ejemplos de solicitudes que puedes ejecutar directamente.

## ⚠️ Disclaimer
Por fines de pruebas se pasa como parámetro el credit amount para forzar el trigger de error con 0.99. Nótese que este valor es cálculado en la capa de lógica de negocio.

