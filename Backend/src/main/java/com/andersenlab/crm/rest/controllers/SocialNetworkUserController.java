package com.andersenlab.crm.rest.controllers;

import com.andersenlab.crm.aop.HasRole;
import com.andersenlab.crm.configuration.swagger.ApiPageable;
import com.andersenlab.crm.model.view.SaleReport;
import com.andersenlab.crm.rest.BaseResponse;
import com.andersenlab.crm.rest.DtoResponse;
import com.andersenlab.crm.rest.dto.SocialNetworkUserDto;
import com.andersenlab.crm.rest.facade.SocialNetworkUserFacade;
import com.andersenlab.crm.rest.resolvers.CrmPredicate;
import com.andersenlab.crm.rest.resolvers.SocialNetworkUserPredicateResolver;
import com.querydsl.core.types.Predicate;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.andersenlab.crm.model.RoleEnum.ROLE_ADMIN;
import static com.andersenlab.crm.model.RoleEnum.ROLE_SALES;
import static com.andersenlab.crm.model.RoleEnum.ROLE_SALES_HEAD;

@RestController
@RequestMapping("/social_user")
@RequiredArgsConstructor
public class SocialNetworkUserController extends BaseController {

    private final SocialNetworkUserFacade socialNetworkUserFacade;

    @HasRole(roles = {ROLE_ADMIN, ROLE_SALES_HEAD})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Создание пользователя социальных сетей",
            notes = "Входящих фильтрующих полей нет.")
    public BaseResponse<DtoResponse<SocialNetworkUserDto>> create(@RequestBody @Valid SocialNetworkUserDto request) {
        return new BaseResponse<>(socialNetworkUserFacade.create(request));
    }


    @HasRole(roles = {ROLE_ADMIN, ROLE_SALES, ROLE_SALES_HEAD})
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Получение пользователя социальных сетей по id",
            notes = "id - обязательное поле (прим. /4).")
    public BaseResponse<SocialNetworkUserDto> getById(@PathVariable("id") Long id) {
        return new BaseResponse<>(socialNetworkUserFacade.getById(id));
    }

    @HasRole(roles = {ROLE_ADMIN, ROLE_SALES, ROLE_SALES_HEAD})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiPageable
    @ApiOperation(value = "Получение списка пользователей соцсетей с возможностью фильтрации",
            notes = "Обязательных полей нет.\n" +
                    "Возможные фильтры по полям:\n id - id пользователя (прим. id=4)\n name - имя пользователя(прим. name=Мария Маркелова)\n" +
                    "createDate - дата создания (При выборе двух createDate, будет выдан результат в промежутке этих дат прим. createDate=2019-01-30T07:45:22&createDate=2022-01-21T10:09:55)\n" +
                    "saleReport.isSaleReportFilter - флаг необходимый для использования фильтров saleReport(должен иметь значение saleReport.isSaleReportFilter=true)\n" +
                    "saleReport.companyName - название компании (Возможен множественный выбор прим. saleReport.companyName=Апполинариум&saleReport.companyName=SITECH УРАЛ)\n" +
                    "saleReport.countryName - имя страны (Возможен множественный выбор прим. saleReport.countryName=Ukraine&saleReport.countryName=Great Britain)\n" +
                    "saleReport.createLeadDate - дата создания лида (Возможен множественный выбор прим. saleReport.createLeadDate=2015-05-14T12:27:16&saleReport.createLeadDate=2016-08-24T06:38:56)\n " +
                    "saleReport.lastActivityDate - дата последней активности (Возможен множественный выбор прим. saleReport.lastActivityDate=2016-07-08T07:15:04&saleReport.lastActivityDate=2015-09-16T06:59:53)\n" +
                    "saleReport.responsibleId - id ответственного сотрудника (Возможен множественный выбор прим. saleReport.responsibleId=18&saleReport.responsibleId=32)\n" +
                    "saleReport.responsibleName - имя ответственного сотрудника (Возможен множественный выбор прим. saleReport.responsibleName=Сергей Баранов&saleReport.responsibleName=Наталья Геллек)\n" +
                    "saleReport.search - поиск по данной строке(по всем полям прим. saleReport.search=Сергей)\n " +
                    "saleReport.socialContactName - имя социального контакта(Возможен множественный выбор прим. saleReport.socialContactName=Hanna Khimich&saleReport.socialContactName=Alexandr Orlov)\n " +
                    "saleReport.sourceId - id источника(Возможен множественный выбор прим. saleReport.sourceId=3&saleReport.sourceId=5)\n " +
                    "saleReport.sourceName - имя источника(Возможен множественный выбор прим. saleReport.sourceName=Почта&saleReport.sourceName=Звонок)\n " +
                    "saleReport.status - статусы (Возможен множественный выбор прим. saleReport.status=Архив&saleReport.status=Лид)\n" +
                    "saleReport.statusChangedDate - дата изменения статуса (Возможен множественный выбор прим. saleReport.statusChangedDate=2014-10-01T05:54:03&saleReport.statusChangedDate=2014-10-08T09:08:08)\n" +
                    "saleReport.type - тип отчёта (Возможен множественный выбор прим. saleReport.type=Оценка&saleReport.type=Резюме)\n " +
                    "saleReport.weight - вес продажи (Возможен множественный выбор прим. saleReport.weight=5&saleReport.weight=7)\n " +
                    "Задание параметров отображения результата:\n page - номер страницы результатов (прим. page=9)\n" +
                    "size - количество записей на странице (прим. size=13)\n sort - сортировка (прим. sort=id,desc)")
    public BaseResponse<Page<SocialNetworkUserDto>> getAll(
            @CrmPredicate(resolver = SocialNetworkUserPredicateResolver.class) Predicate predicate,
            Pageable pageable) {
        return new BaseResponse<>(socialNetworkUserFacade.getAll(predicate, pageable));
    }

    @HasRole(roles = {ROLE_SALES_HEAD, ROLE_SALES})
    @GetMapping("/get_sale_report_users")
    @ApiPageable
    @ApiOperation(value = "Получение отчета о продажах компаний с возможностью фильтрации",
            notes = "Обязательных полей нет\n" +
                    "Возможные фильтры по полям:\n id - id отчёта (Возможен множественный выбор прим. id=139&id=209)\n companyId - id компании (Возможен множественный выбор прим. companyId=6514&companyId=68)\n companyName - название компании (Возможен множественный выбор прим. companyName=deliverator&companyName=ALPOL)\n" +
                    "companyUrl - сайт компании (Возможен множественный выбор прим. companyUrl=http://www.taxiotra.ru&companyUrl=http://xcom.ru)\n contactPosition - занимаемая должность(Возможен множественный выбор прим. contactPosition=проректор&contactPosition=СOO)\n" +
                    "countryId - id страны (Возможен множественный выбор прим. countryId=98&countryId=341)\n countryName - имя страны (Возможен множественный выбор прим. name=Ukraine&name=Great Britain)\n createDate - дата создания продажи (При вводе двух дат будет выбран промежуток прим. createDate=2014-12-17T10:14:20&createDate=2017-08-11T05:52:50)\n" +
                    "createLeadDate - дата создания лида (При вводе двух дат будет выбран промежуток прим. createLeadDate=2014-12-17T10:14:20&createLeadDate=2017-08-11T05:52:50)\n email - емейл контактного лица (Возможные значения: null - все записи с пустым значением поля, notNull - все записи, в которых поле заполнено)\n" +
                    "estimationRequests - запросы оценки (Возможен множественный выбор прим. estimationRequests=3564  VZperformance - Desktop app&estimationRequests=783 Site web/boutique en ligne pour bijoux «sens bons»)\n excludedStatus - исключенные статусы (Возможен множественный выбор прим. excludedStatus=Лид&excludedStatus=Архив)\n" +
                    "lastActivityDate - дата последней активности (Возможен множественный выбор прим. lastActivityDate=2018-11-08T11:30:55&lastActivityDate=2019-04-29T13:51:34)\n mainContact - главный контакт (Возможен множественный выбор прим. mainContact=Aberlt Wu&mainContact=Tanel Hiir)\n " +
                    "personalEmail - личный емейл контактного лица (Возможные значения: null - все записи с пустым значением поля, notNull - все записи, в которых поле заполнено)\n phone - номер телефона (Возможные значения: null - все записи с пустым значением поля, notNull - все записи, в которых поле заполнено)\n responsibleId - id ответственного сотрудника (Возможен множественный выбор прим. responsibleId=25&responsibleId=172)\n" +
                    "responsibleName - имя ответственного сотрудника (Возможен множественный выбор прим. responsibleName=Ярослав Веремейчик&responsibleName=Дмитрий Малиновский)\n resumeRequests - резюме запросов (Возможен множественный выбор прим. resumeRequests=1664 Node.js&resumeRequests=3843 Oxid (PHP))\n " +
                    "search - поиск по данной строке(по всем полям прим. search=Сергей)\n skype - скайп (Возможные значения: null - все записи с пустым значением поля, notNull - все записи, в которых поле заполнено)\n socialContactId - id социального контакта (Возможен множественный выбор прим. socialContactId=17&socialContactId=135)\n" +
                    "socialContactName - имя социального контакта(прим. socialContactName=Hanna Khimich&socialContactName=огонь)\n socialNetwork - социальная сеть(Возможен множественный выбор прим. socialNetwork=https://www.linkedin.com/in/antonklees/&socialNetwork=https://www.linkedin.com/in/matic-hribar-2181b899/)\n" +
                    "sourceId - id источника(Возможен множественный выбор прим. sourceId=3&sourceId=4)\n sourceName - имя источника(Возможен множественный выбор прим. sourceName=Почта&sourceName=Личный контакт)\n status - статусы (Возможен множественный выбор прим. status=Архив&status=Лид)\n " +
                    "statusChangedDate - дата изменения статуса (При вводе двух дат будет выбран промежуток прим. statusChangedDate=2013-03-20T14:54:24&statusChangedDate=2022-08-16T14:41:01)\n statusDate - дата создания статуса (Возможен множественный выбор прим. statusDate=2019-03-20&statusDate=2018-08-29)\n" +
                    "type - тип отчёта (Возможен множественный выбор прим. type=Оценка&type=Резюме/Оценка)\n weight - вес продажи (Возможен множественный выбор прим. weight=5&weight=5)\n " +
                    "Задание параметров отображения результата:\n page - номер страницы результатов (прим. page=9)\n" +
                    "size - количество записей на странице (прим. size=13)\n sort - сортировка (прим. sort=id,desc)")
    public BaseResponse<Page<String>> getSaleReportCompanies(
            @QuerydslPredicate(root = SaleReport.class) Predicate predicate,
            Pageable pageable) {
        return new BaseResponse<>(socialNetworkUserFacade.getAllBySaleReport(predicate, pageable));
    }

    @HasRole(roles = ROLE_ADMIN)
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Изменение пользователя соцсетей и перезапись его в БД",
            notes = "id - обязательное поле (прим. /4).")
    public BaseResponse<SocialNetworkUserDto> updateAnswer(
            @PathVariable("id") Long id,
            @RequestBody @Valid SocialNetworkUserDto dto) {
        return new BaseResponse<>(socialNetworkUserFacade.update(id, dto));
    }

    @HasRole(roles = ROLE_ADMIN)
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Удаление пользователя соцсети по id",
            notes = "id - обязательное поле (прим. /4).")
    public BaseResponse deleteAnswers(
            @ApiParam(value = "For example: 1")
            @PathVariable("id") Long id) {
        socialNetworkUserFacade.delete(id);
        return new BaseResponse<>();
    }
}
