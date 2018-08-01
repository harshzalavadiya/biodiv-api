
#2ndFeb2017


alter table activity_feed add column description_json json;
update activity_feed set description_json=row_to_json(row) from (select id as aid, activity_descrption as description from activity_feed af) row where row.description is not null and row.aid=id;


#OBSERVATION CREATED

update activity_feed set description_json = row_to_json(row)
from ( select af.id as aid,'Observation created' as activity_performed,'true' as is_migrated  from activity_feed af) row
where row.aid =id and activity_type = 'Observation created';


#USERGROUP


update activity_feed
set description_json =  row_to_json(row)
from( select af.id as aid ,af.activity_descrption as activity_performed, ug.name as name , af.activity_holder_id as ro_id ,'true' as is_migrated from activity_feed af inner join user_group ug on af.activity_holder_id = ug.id ) row
where row.aid = id and (activity_type ='Posted resource' or activity_type = 'Removed resoruce');


#SUGGESTED/REMOVED SPECIES NAME


update activity_feed
set description_json = row_to_json(row)
from (select af.id as aid , af.activity_descrption as description , af.activity_type as activity_performed,'true' as is_migrated from activity_feed af) row
where  row.aid = id and activity_type = 'Suggested species name';


update activity_feed
set description_json = row_to_json(row)
from (select af.id as aid , af.activity_descrption as description , 'Removed species name' as activity_performed,'true' as is_migrated from activity_feed af) row
where  row.aid = id and  activity_type = 'Suggestion removed';



update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,af.activity_type as activity_performed,r.name as name , td.species_id as ro_id ,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row
where row.aid = id and activity_type = 'Suggested species name';


update activity_feed
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,af.activity_type as activity_performed ,rv.given_sci_name as name , td.species_id as ro_id ,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row
where row.name is not null and row.aid = id and activity_type = 'Suggested species name';


#OBSERVATION TAG UPDATED

update activity_feed
set description_json = row_to_json(row)
from ( select af.id as aid,af.activity_descrption as description,'Tag updated' as activity_performed,'true' as is_migrated from activity_feed af) row
 where row.aid =id and activity_type='Observation tag updated';


#CUSTOM FIELD EDITED

update activity_feed                                                          
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description , af.activity_type as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Custom field edited';


#ADDED A Fact


update activity_feed                                                          
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description , af.activity_type as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Added a fact';


#FEATURED AND UNFEATURED


update activity_feed
set description_json = row_to_json(row)
from ( select af.id as aid , af.activity_descrption as description ,'Removed featured observation from group' as activity_performed , ug.name as name , af.activity_holder_id as ro_id , 'true' as is_migrated from activity_feed af inner join user_group ug on af.activity_holder_id = ug.id) row
where row.aid = id and activity_type = 'UnFeatured' and activity_holder_type = 'species.groups.UserGroup' and root_holder_type='species.participation.Observation';


update activity_feed
set description_json = row_to_json(row)
from ( select af.id as aid , af.activity_descrption as description ,'Removed featured observation in' as activity_performed , 'India Biodiversity Portal' as name , 'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'UnFeatured' and activity_holder_type = 'species.participation.Observation' and root_holder_type='species.participation.Observation';

 update activity_feed
set description_json = row_to_json(row)
from ( select af.id as aid , af.activity_descrption as description ,'Featured observation to group' as activity_performed , ug.name as name ,af.activity_holder_id as ro_id, 'true' as is_migrated from activity_feed af inner join user_group ug on af.activity_holder_id = ug.id) row
where row.aid = id and activity_type = 'Featured' and activity_holder_type = 'species.groups.UserGroup' and root_holder_type='species.participation.Observation';

update activity_feed
set description_json = row_to_json(row)
from ( select af.id as aid , af.activity_descrption as description ,'Featured observation in' as activity_performed , 'India Biodiversity Portal' as name , 'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Featured' and activity_holder_type = 'species.participation.Observation' and root_holder_type='species.participation.Observation';



#OBSERVATION UPDATED


update activity_feed
set description_json = row_to_json(row)
from( select af.id as aid , 'User updated the observation details' as description , af.activity_type as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Observation updated';


#FLAGGED


update activity_feed
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description , 'Observation flagged' as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Flagged' and root_holder_type = 'species.participation.Observation';


#OBV UNLOCKED

update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,'Unlocked species name' as activity_performed, r.name as name ,td.species_id as ro_id,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row 
where row.name is not null and row.aid = id and activity_type = 'obv unlocked';

update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,'Unlocked species name' as activity_performed, rv.given_sci_name as name ,td.species_id as ro_id,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row 
where row.name is not null and row.aid = id and activity_type = 'obv unlocked';


#OBV LOCKED

update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,'Validated and locked species name' as activity_performed, r.name as name ,td.species_id as ro_id,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row 
where row.aid = id and activity_type = 'obv locked';


update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,'Validated and locked species name' as activity_performed, rv.given_sci_name as name ,td.species_id as ro_id,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row 
where row.name is not null and row.aid = id and activity_type = 'obv locked';


#UPDATED FACT


update activity_feed                                                          
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description , af.activity_type as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Updated fact';


#FLAG REMOVED

update activity_feed                                                    
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,'Observation flag removed' as activity_performed,'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Flag removed';


#AGREED ON SPECIES NAME

update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,af.activity_type as activity_performed,r.name as name , td.species_id as ro_id ,'species' as ro_type,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row
where row.aid = id and activity_type = 'Agreed on species name';


update activity_feed                            
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description ,af.activity_type as activity_performed,rv.given_sci_name as name , td.species_id as ro_id ,'species' as ro_type ,r.is_scientific_name as is_scientific_name , 'true' as is_migrated from activity_feed af inner join recommendation_vote rv on af.activity_holder_id = rv.id inner join recommendation r on rv.recommendation_id = r.id left join taxonomy_definition td on r.taxon_concept_id = td.id) row
where row.name is not null and row.aid = id and activity_type = 'Agreed on species name';


#OBSERVATION SPECIES GROUP UPDATED

update activity_feed                                                          
set description_json = row_to_json(row)
from( select af.id as aid , af.activity_descrption as description , 'Species group updated' as activity_performed , 'true' as is_migrated from activity_feed af) row
where row.aid = id and activity_type = 'Observation species group updated' and root_holder_type='species.participation.Observation';



#2Feb2018   remove initial " in filter rule for it to get parsed.
#repeat for all usergroups with filter rules

#Assam filter rule
\o '/tmp/4087136_geometry.txt'
select filter_rule from user_group where id=4087136;
\q
vim '/tmp/4087136_geometry.txt'
\set content `cat /tmp/4087136_geometry.txt`
update user_group set filter_rule=:'content' where id=4087136;

#Konkan filter rule
\o '/tmp/4256157_geometry.txt'
select filter_rule from user_group where id=4256157;
vim /tmp/4256157_geometry.txt
\set content `cat /tmp/4256157_geometry.txt`
update user_group set filter_rule=:'content' where id=4256157;

#Remove size limit on filter_rule as spatial co-ordinates can be very long
ALTER table download_log ALTER COLUMN filter_url TYPE varchar;
