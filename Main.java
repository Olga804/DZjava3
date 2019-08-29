package com.company;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;


public class Main {


    public static void main(String[] args)  {

    Gson gson = new Gson();
    LocalDate today = LocalDate.now();
    Scanner scanner = new Scanner(System.in);
    String path = "C:\\example.json";
    if (path==""){
        System.out.print("Введите имя файла:  ");
        path = scanner.nextLine();
    }

    try (Reader reader = new FileReader(path)){
        List<Organization> list = gson.fromJson(reader, new TypeToken<List<Organization>>(){}.getType());

        String string = new String();
        long count = 0;


        //1 Вывод всех компаний(Название - дата основания)
        count=list.stream().count();
        if (count>0){
            System.out.print("Краткое название компании/");
            System.out.println("Дата основания");
            list.stream()
                    .forEach(x -> {
                        String[] help = x.getEgrul_date().split("\\.|,|/|-");
                        Stream.of(String.format("%s - %s/%s/%s", x.getName_short(), help[2],help[1],help[0]))
                                .forEach(System.out::println);
                    });
        } else{
            System.out.println("В таблице нет элементов!");
        }

        System.out.println();

        //2 просроченые ценные бумаги, и сумма таких(код, дата истечения, полное название компании)
       count =
        list.stream()
                .flatMap(x->Arrays.stream(x.getSecurities()))
                .filter(x-> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate date = LocalDate.parse(x.getDate_to(), formatter);
                            return today.isAfter(date);
                        })
                .count();

           if(count>0){
            System.out.println("Просроченные ценные бумаги:");
            System.out.print("   Код      /");
            System.out.print("    Дата   /");
            System.out.println("  Полное название компании");
            list.stream()
                    .forEach(x->{Arrays.stream(x.getSecurities())
                    .filter(z-> {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate date = LocalDate.parse(z.getDate_to(), formatter);
                        return today.isAfter(date);
                })  .flatMap(y->Stream.of(String.format("%s %s %s", y.getCode(),y.getDate_to(),x.getName_full())))
                    .forEach(System.out::println);

            });
            System.out.println("Всего: " + count);


        } else {
            System.out.println("Нет элементов");
        }
        System.out.println();

        try{//3 Поиск по дате (название и дату основания)
        System.out.print("Поиск по дате основания.");
        System.out.print("Введите дату: ");
        string = scanner.nextLine();

        String[] help = string.split("\\.|,|/");
        int year = Integer.parseInt(help[2]);

        if(year<100){
        if(year<today.getYear()%100){
            year+=2000;
        } else {
            year+=1900;
        }}

        final LocalDate dayX = LocalDate.of(year,Integer.parseInt(help[1]),Integer.parseInt(help[0]));
        List<String>raft = list.stream()
                .filter(z-> {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate date = LocalDate.parse(z.getEgrul_date(), formatter);
                            return dayX.isBefore(date);
                        })
                .flatMap(x -> Stream.of(String.format("%s %s", x.getName_full(),x.getEgrul_date())))
               .collect(Collectors.toList());

       if(raft.stream().count()>0) {
           raft.stream().forEach(System.out::println);
       } else {
           System.out.println("Компаний основанных после введенной даты не найдено!");
       }}catch(Exception e){
            System.out.println("ОШИБКА! Не корpектная дата!");
        }


        //4 Запрос по валюте( id, code бумаги)
        System.out.print("Поиск по валюте.");
        System.out.print("Введите код валюты: ");

        string = scanner.nextLine();
        final String stringMoney = string;

        List<String>raft = list.stream()
                .flatMap(x->Arrays.stream(x.getSecurities()))
                        .filter(y->y.getCurrency().getCode().equals(stringMoney))
                        .flatMap(z->Stream.of(String.format("%s %s", z.getId(),z.getCode())))
                        .collect(Collectors.toList());
        if (raft.stream().count()>0){
            System.out.print("    id    /");
            System.out.println("   code   ");
            raft.stream().forEach(System.out::println);
        }else {

            System.out.println("Такой валюты не найдено!");
        }




    }catch (IOException e){
        System.out.println("ОШИБКА! Файл *.json не найден!");

    }

    }
}





