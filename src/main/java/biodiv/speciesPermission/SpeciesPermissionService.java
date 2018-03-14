package biodiv.speciesPermission;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import biodiv.common.AbstractService;
import biodiv.speciesPermission.SpeciesPermission.PermissionType;
import biodiv.taxon.datamodel.dao.Taxon;
import biodiv.taxon.service.TaxonService;
import biodiv.user.User;


public class SpeciesPermissionService extends AbstractService<SpeciesPermission>{

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private SpeciesPermissionDao speciesPermissionDao;
	
	@Inject
	TaxonService taxonService;

	@Inject
	SpeciesPermissionService(SpeciesPermissionDao speciesPermissionDao) {
		super(speciesPermissionDao);
		this.speciesPermissionDao = speciesPermissionDao;
		log.trace("UserGroupService constructor");
	}

	public List<Taxon> parentTaxon(Taxon taxon){
		List<Taxon> result =  new ArrayList<Taxon>();
		
		result=taxonService.getAllByTaxonDefinition(taxon.getId());
		
		return result;
	}
	
	public Boolean isTaxonContributor(Taxon taxon, User currentUser, List<PermissionType> permissionTypes) {
		if(currentUser == null) return false;
		if(taxon == null) return false;
		List<Taxon> parentTaxons = parentTaxon(taxon);
		parentTaxons.add(taxon);
		return isTaxonContributor( parentTaxons,currentUser, permissionTypes);
	}

	private Boolean isTaxonContributor(List<Taxon> parentTaxons, User currentUser,List<PermissionType> permissionTypes) {
		
		if(parentTaxons == null || currentUser == null || permissionTypes == null) return false;
		List<Long> parentTaxonIds = new ArrayList<Long>();
		for(Taxon t : parentTaxons){
			parentTaxonIds.add(t.getId());
		}
		List<String> permissions = new ArrayList<String>();
		for(PermissionType pt: permissionTypes){
			permissions.add(pt.value());
		}
		List<SpeciesPermission> res = speciesPermissionDao.getAllSpeciesPermission(currentUser,permissions,parentTaxonIds);
		if(res != null && res.size()>0){
			return true;
		}else return false;
	}

}
