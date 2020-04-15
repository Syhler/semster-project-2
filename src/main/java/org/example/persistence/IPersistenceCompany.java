package org.example.persistence;

import org.example.entity.CompanyEntity;
import org.example.entity.Role;
import org.example.entity.UserEntity;

import java.util.List;

public interface IPersistenceCompany
{

    List<CompanyEntity> getCompanies();
    boolean deleteCompany(CompanyEntity companyEntity);
    boolean updateCompany(CompanyEntity companyEntity);
    long createCompany(CompanyEntity companyEntity);

}