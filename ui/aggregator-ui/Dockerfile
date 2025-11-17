FROM node:latest AS build

WORKDIR /usr/local/app

COPY package*.json ./

RUN npm install

COPY ./ /usr/local/app/

RUN npm run build

FROM nginx:latest

COPY --from=build /usr/local/app/dist/aggregator-ui/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
