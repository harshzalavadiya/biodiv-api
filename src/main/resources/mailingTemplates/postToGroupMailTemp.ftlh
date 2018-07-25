<table class="head-wrap" bgcolor="#ECE9B7" style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;width: 100%; border-top: 1px solid #A1A376; border-left: 1px solid #A1A376; border-right: 1px solid #A1A376; width:610px;">
	<tr style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;">
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
		<td class="header container" style="margin: 0 auto;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;display: block;max-width: 600px;clear: both;">
			
			<div class="content" style="margin: 0 auto;padding: 2px;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;max-width: 600px;display: block; text-align:center;">
                            <a style="margin: 0;padding: 0;font-family: &quot;HelveticaNeue-Light&quot;, &quot;Helvetica Neue Light&quot;, &quot;Helvetica Neue&quot;, Helvetica, Arial, &quot;Lucida Grande&quot;, sans-serif;line-height: 1;color: #000;font-weight: 200;font-size: 20px; text-decoration:none;">${siteName}</a>
			</div>
			
		</td>
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
	</tr>
</table>

<table class="m_-9081882762390516907m_5602930087336413147body-wrap" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:100%;border:1px solid #a1a376;width:610px">
	<tbody><tr style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">
		<td style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif"></td>
		<td class="m_-9081882762390516907m_5602930087336413147container" bgcolor="#FFFFFF" style="margin:0 auto;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;display:block;max-width:600px;clear:both">

			<div class="m_-9081882762390516907m_5602930087336413147content" style="margin:0 auto;padding:2px;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;max-width:610px;display:block">
				<table style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:100%">
					<tbody>
						<tr style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">
							<td style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">
								<p class="m_-9081882762390516907m_5602930087336413147lead" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;margin-bottom:10px;font-weight:normal;font-size:14px;line-height:1">
									Hi ${follower.name},<br><br>
	
									<a href="${serverUrl}/user/show/${whoPosted.id?c}" ><img src="${serverUrl}/biodiv/users/${whoPosted.icon}" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;max-width:30px;max-height:30px;display:inline-block;vertical-align:middle" class="CToWUd"></a>					
									
									<a href="${serverUrl}/user/show/${whoPosted.id?c}" > ${whoPosted.name}</a>		
											<#if submitType == "post">posted<#else>removed </#if> observation <#if submitType == "post">to<#else>from </#if> group 
											
											<#list wherePosted as where>
											<a href="${serverUrl}/group/${where.webaddress}/show" ><i>${where.name}</i></a>									
											</#list>
								</p>
		
							</td>
						</tr>
					</tbody>
				</table>
			</div><span class="im"><span>
				
			
			<div class="m_-9081882762390516907m_5602930087336413147column-wrap" style="margin:0 auto;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;max-width:600px;background-color:#d4ece3">
			
				<div class="m_-9081882762390516907m_5602930087336413147column" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:135px;float:left">
					<table align="left" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:100%">
						<tbody><tr style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">
							<td style="margin:0;padding:2px;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">				

								<a href="${serverUrl}/observation/show/${whatPosted.id?c}" ><img src=${whatPosted.icon} style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;max-width:130px;max-height:130px;display:inline-block;vertical-align:middle"></a>
							</td>
						</tr>
					</tbody></table>
				</div>
			
				<div class="m_-9081882762390516907m_5602930087336413147column" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:450px;float:left;background-color:#d4ece3">
					<table align="right" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;width:100%">
						<tbody>
							<tr style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">
								<td style="margin:0;padding:2px;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif">				
																
									<p style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;margin-bottom:0px;font-weight:normal;font-size:14px;line-height:1.4">
	
									
									
	
									
										<b>Name:</b> ${whatPosted.name}<br>
									
	
	
									
	                                
	                                    <b>Location: </b> ${whatPosted.location}<br>
	                                
	                                
	                                    <b>Observed On:</b> ${whatPosted.observedOn}<br>
	                                
									</p>
	
			
									
								</td>
							</tr>
						</tbody>
					</table>	
				</div>

				<div class="m_-9081882762390516907m_5602930087336413147clear" style="margin:0 auto;padding:10px 0px;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;max-width:600px;display:block;background-color:#d4ece3;clear:both">
							
							<p class="m_-9081882762390516907m_5602930087336413147callout" style="margin:0;padding:0 5px;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;margin-bottom:2px;font-weight:normal;font-size:14px;line-height:1;background-color:#d4ece3">

									For more information, please visit the page <a href="${serverUrl}/observation/show/${whatPosted.id?c}" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;color:#2ba6cb;font-weight:bold" target="_blank" data-saferedirecturl="https://www.google.com/url?hl=en&amp;q=http://indiabiodiversity.org/observation/show/1770256&amp;source=gmail&amp;ust=1521876944507000&amp;usg=AFQjCNHfSiPtayrlu3f6rjTjXtG-1Hr4eg">here »</a>							
							</p>
				</div>
			</div>	

				
				<div class="m_-9081882762390516907m_5602930087336413147clear" style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif;clear:both;background-color:#d4ece3">
					<a style="padding:0 2px">The above observation is a member of the following groups: </a><a><br>
						
							</a>
						<#list whatPosted.userGroups as memberOf>
							<div style="height:30px;width:186px;border:1px solid #a1a376;background-color:#d4ece3;float:left;margin:2px 6px">
							    <a href="${serverUrl}/group/${memberOf.webaddress}/show" style="text-decoration:none;color:#222222"> 
								<img src="${serverUrl}/biodiv/userGroups/${memberOf.icon}" style="width:30px;height:30px;vertical-align:middle" class="CToWUd">

									

									 
										
											${memberOf.name} <br>
										
	
									
							    </a>   
							</div>
						</#list>
						
						</div>	
					
						

			
			
		</span></span></td>
		<td style="margin:0;padding:0;font-family:&quot;Helvetica Neue&quot;,&quot;Helvetica&quot;,Helvetica,Arial,sans-serif"></td>
	</tr>
</tbody></table>



<table class="footer-wrap" style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;width: 100%;clear: both;border-left: 1px solid #A1A376; border-right: 1px solid #A1A376; border-bottom: 1px solid #A1A376; width:610px;">


	<tr style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;">
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
		<td class="container" style="margin: 0 auto;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;display: block;max-width: 600px;clear: both;">
		

		
		</td>
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
	</tr>


	<tr style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;">
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
		<td class="container" style="margin: 0 auto;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;display: block;max-width: 600px;clear: both;">
			
				<!-- content -->
				<div class="content" style="margin: 0 auto;padding: 0px;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;max-width: 600px;display: block;">
					<table style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;width: 100%;">
						<tr style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;">
							<td align="left" style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;">
								<p style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;margin-bottom: 10px;font-weight: normal;font-size: 12px;line-height: 1;">
									If you don't want to recieve notifications from our portal, please unsubscribe by logging into <a href="${serverUrl}/user/show/${follower.id?c}" style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;color: #2BA6CB;"><unsubscribe style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"> your profile</unsubscribe></a>
								</p>
							</td>
						</tr>
					</table>
				</div><!-- /content -->
				
		</td>
		<td style="margin: 0;padding: 0;font-family: &quot;Helvetica Neue&quot;, &quot;Helvetica&quot;, Helvetica, Arial, sans-serif;"></td>
	</tr>



</table>