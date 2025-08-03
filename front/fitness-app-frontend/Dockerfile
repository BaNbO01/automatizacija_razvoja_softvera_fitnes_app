# Prva faza: build-ovanje aplikacije
FROM node:18-alpine AS builder

# Postavljanje radnog direktorijuma
WORKDIR /app

# Kopiranje package.json i package-lock.json da bi se keširale zavisnosti
COPY package*.json ./

# Instaliranje zavisnosti
RUN npm install

# Kopiranje izvornog koda
COPY . .

# Build-ovanje aplikacije za produkciju
RUN npm run build

# Druga faza: serviranje aplikacije sa Nginx-om
FROM nginx:alpine

# Kopiranje build-ovanih fajlova u Nginx folder
COPY --from=builder /app/build /usr/share/nginx/html

# Izlaganje porta 80
EXPOSE 80

# Pokretanje Nginx-a
CMD ["nginx", "-g", "daemon off;"]
