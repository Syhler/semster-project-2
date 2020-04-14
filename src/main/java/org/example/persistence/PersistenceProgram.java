package org.example.persistence;

import org.example.entity.CompanyEntity;
import org.example.entity.CreditEntity;
import org.example.entity.ProgramEntity;
import org.example.entity.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceProgram extends BasePersistence implements IPersistenceProgram {

    private Connection connection;

    public PersistenceProgram() {
        connection = initializeDatabase();
    }

    /**
     * inserts given data into the programInformation table in the database
     * @param programEntity
     * @param language_id
     * @return
     */
    private long programInformation(ProgramEntity programEntity, long language_id) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO programInformation" +
                    "(description, title,language_id,program_id ) VALUES (?,?,?,?) RETURNING id");
            stmt.setString(1, programEntity.getDescription());
            stmt.setString(2, programEntity.getName());
            stmt.setLong(3, language_id);
            stmt.setLong(4, programEntity.getId());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                // 1 is the index for id
                return resultSet.getLong("id");
            }
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

    }


    /**
     * Inserts given data into the companyProgram table in the database
     * @param program_id
     * @param company_id
     * @return
     */
    private boolean companyProgram(long program_id, long company_id) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO companyProgram(program,company)" + "VALUES (?,?)");
            stmt.setLong(1, program_id);
            stmt.setLong(2, company_id);
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a null timestamp into the program table in the database so that a program id is set.
     * @return the programs id.
     */
    private long insertProgram(){
       try {
           PreparedStatement statement = connection.prepareStatement(
                   "INSERT INTO program(timestamp_for_deletion) values (? ) RETURNING id");
           statement.setTimestamp(1,null);
           ResultSet resultSet = statement.executeQuery();
           if (resultSet.next()) {
               return resultSet.getLong("id");
           }
           return 0;
       } catch (SQLException e){
           e.printStackTrace();
           return 0;
       }

    }

    /**
     * Inserts all producers on the given program into the programproducer table in the database.
     * @param producer
     * @param programId
     */
    private void insertProducer(List<UserEntity> producer,long programId){
        try {
            PreparedStatement statementProducer = connection.prepareStatement(
                    "insert INTO programproducer(program_id, producer_id) " + "values (?,?)");
            connection.setAutoCommit(false);
            for (UserEntity user : producer) {
                statementProducer.setLong(1,programId);
                statementProducer.setLong(2,user.getId());
                statementProducer.addBatch();
            }
           statementProducer.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Inserts all credits on the given program into the credit table in the database.
     * @param credits
     * @param programId
     */
    private void insertCredit(List<CreditEntity> credits, long programId){
        try {
            PreparedStatement statementCredit  = connection.prepareStatement("INSERT INTO credit(program,\"user\") "
                    +"values(?,?)");
            connection.setAutoCommit(false);
        for ( CreditEntity credit: credits) {

            statementCredit.setLong(1,programId);
            statementCredit.setLong(2,credit.getActor().getId());
            statementCredit.addBatch();
            }
        statementCredit.executeBatch();
        connection.commit();
        connection.setAutoCommit(true);
        }catch (SQLException e){
            e.printStackTrace();

        }

    }



    @Override
    public boolean createProgram(ProgramEntity programEntity) {

        programEntity.setId(insertProgram());
        programInformation(programEntity,1);
        companyProgram(programEntity.getCompany().getId(), programEntity.getId());
        insertProducer(programEntity.getProducer(),programEntity.getId());
        insertCredit(programEntity.getCredits(),programEntity.getId());

        return true;
    }

    //soft deleting instead of just outright deleting
    /*public boolean deleteCredit(ProgramEntity programEntity){
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM credit WHERE program =?");

            statement.setLong(1,programEntity.getId());

            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }*/

    /*public boolean deletecompanyprogram(ProgramEntity programEntity) {
        try {
            PreparedStatement  statement = connection.prepareStatement("DELETE FROM companyprogram  " +
                    "WHERE companyprogram.program = ?");

            statement.setLong(1,programEntity.getId());
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }*/

    /* public boolean deleteprogramproducer(ProgramEntity programEntity){
        try {
            PreparedStatement statement =connection.prepareStatement("DELETE FROM programproducer where program_id = ? ");
            statement.setLong(1,programEntity.getId());
            return  statement.execute();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }*/

    /*public boolean deleteProgramTable(ProgramEntity programEntity){

        try {
            PreparedStatement statement = connection.prepareStatement("DELETE program, programinformation FROM program INNER JOIN " +
                    "programinformation where program.id = ? and programinformation.id = program.programinformation");
            statement.setLong(1,programEntity.getId());
            return statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }*/

    /**
     * Sets a timestamp for when the program was deleted, doesnt delete anything in the database.
     * @param programID
     * @return
     */
    public boolean softDeleteProgramTable(long programID){
        try {
            PreparedStatement statement =connection.prepareStatement("UPDATE program SET timestamp_for_deletion = ? " +
                    "WHERE id = ? ");

            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            statement.setLong(2,programID);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * uses other methods to determine how a program is deleted
     * @param programEntity
     * @return
     */
    @Override
    public boolean deleteProgram(ProgramEntity programEntity) {
        softDeleteProgramTable(programEntity.getId());

        return true;
    }

    /**
     * updates a programs infomation in the programinformation table.
     * @param programEntity
     * @return
     */
    @Override
    public boolean updateProgram(ProgramEntity programEntity) {
        try {
            PreparedStatement stmt = connection.prepareStatement("UPDATE  programinformation SET title = ?, description = ? " +
                    " WHERE programinformation.id = ? ");
            stmt.setString(1,programEntity.getName());
            stmt.setString(2,programEntity.getDescription());
            stmt.setLong(3,programEntity.getPrograminformationId());
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Selects information on every actor on a given program.
     * @param programEntity
     * @return list of all the actors on a program.
     */
    public ArrayList<CreditEntity> getCreditUser(ProgramEntity programEntity){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT credit.program, \"user\".id, \"user\".title, \"user\"" +
                    ".firstname, \"user\".middlename ,\"user\".lastname,\"user\".email from credit inner join " +
                    "\"user\" on credit.\"user\" = \"user\".id where credit.program = ? ");
            statement.setLong(1,programEntity.getId());
            ResultSet resultSet = statement.executeQuery();
            ArrayList<CreditEntity> creditList = new ArrayList<>();
            while (resultSet.next()){
               CreditEntity credit = new CreditEntity(programEntity.getId(),createUserEntityFromResultSet(resultSet));
               creditList.add(credit);
            }
            return creditList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Selects information on all producers on a given program
     * @param programEntity
     * @return list of all the users on a program.
     */
    public ArrayList<UserEntity> getProducerforProgram(ProgramEntity programEntity){
        try {
            PreparedStatement statement = connection.prepareStatement("select \"user\".id, \"user\".title,firstname, middlename, lastname, " +
                    "email from programproducer inner join \"user\" on programproducer.producer_id = \"user\".id " +
                    "where programproducer.program_id = ?");
            statement.setLong(1,programEntity.getId());
            ResultSet resultSet = statement.executeQuery();
            ArrayList<UserEntity> producerList = new ArrayList<>();
            while (resultSet.next()){
            UserEntity producer = createUserEntityFromResultSet(resultSet);
            producerList.add(producer);
            }
            return producerList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * Selects information the company that is connected to the program
     * @param programEntity
     * @return A company entity
     */
    public CompanyEntity getCompanyProgram(ProgramEntity programEntity){
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT company.name , company.id from company" +
                    " inner join companyprogram  on company.id = companyprogram.company where program = ?");
            statement.setLong(1,programEntity.getId());
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            CompanyEntity company = new CompanyEntity(resultSet.getLong("id"),resultSet.getString("name"));


            return company;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    /**
     * Returns information based on a given programs id.
     * @param programEntity
     * @return A progrmaentity
     */
    @Override
    public ProgramEntity getProgramById(ProgramEntity programEntity) {
        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT programinformation.description ," +
                    " programinformation.title , programinformation.program_id FROM programinformation, program  where programinformation.program_id = ? ");
            stmt.setLong(1, programEntity.getId());

            var resultSet = stmt.executeQuery();

            // checks if resultset contains any rows
            if (!resultSet.next()) {
                return null;
            }
            ProgramEntity returnValue = new ProgramEntity(resultSet.getLong("program_id"),resultSet.getString("title"),
                    resultSet.getString("description"), getCompanyProgram(programEntity),
                    getProducerforProgram(programEntity), getCreditUser(programEntity));

            return returnValue;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns title and id for all programs in the database.
     * @return all programs in the database.
     */
    @Override
    public List<ProgramEntity> getAllPrograms() {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("SELECT distinct on (programinformation.program_id) programinformation.title , " +
                    "programinformation.program_id from programinformation");

            ResultSet sqlReturnValues = stmt.executeQuery();
            List<ProgramEntity> returnvalue = new ArrayList<>();

            while (sqlReturnValues.next()) {
                
                returnvalue.add(new ProgramEntity(sqlReturnValues.getLong("program_id"),sqlReturnValues.getString("title")));

            }
            return returnvalue;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all programs a given producer has been involved with
     * @param producer
     * @return A list of programentitys
     */
    @Override
    public List<ProgramEntity> getProgramsByProducer(UserEntity producer) {

        try {
            PreparedStatement stmt = connection.prepareStatement("SELECT producer_id, programproducer.program_id, title from programproducer" +
                    " inner join programinformation on programproducer.program_id = programinformation.program_id " +
                    "where programproducer.producer_id = ?");
            stmt.setLong(1, producer.getId());
            ResultSet sqlReturn = stmt.executeQuery();
            ArrayList<ProgramEntity> programList = new ArrayList<>();
           while (sqlReturn.next()){
               ProgramEntity program = new ProgramEntity(sqlReturn.getLong("program_id"), sqlReturn.getString("title"));
               programList.add(program);

           }

            return programList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all programs a company has been involved with
     * @param companyEntity
     * @return A list of all programs a company has been involved with.
     */
    @Override
    public List<ProgramEntity> getProgramsByCompany(CompanyEntity companyEntity) {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("select title, company, program from companyprogram " +
                    "inner join programinformation on companyprogram.id = programinformation.program_id " +
                    "where companyprogram.company = ?");
            stmt.setLong(1, companyEntity.getId());
            ResultSet sqlReturn = stmt.executeQuery();

            ArrayList<ProgramEntity> programList = new ArrayList<>();
            while (sqlReturn.next()){
                ProgramEntity program = new ProgramEntity(sqlReturn.getLong("program"),sqlReturn.getString("title"));
                programList.add(program);
            }

            return programList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Creates a userEntity based on a given resultset.
     * @param resultSet
     * @return A user.
     * @throws SQLException
     */
    private UserEntity createUserEntityFromResultSet(ResultSet resultSet) throws SQLException {
        var user = new UserEntity(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("firstName"),
                resultSet.getString("middleName"),
                resultSet.getString("lastName"),
                resultSet.getString("email"));
        return user;

    }
}
