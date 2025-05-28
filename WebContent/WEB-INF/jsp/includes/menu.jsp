<div class="navbar navbar-default" style="border-color: #f8f8f8;">
	<div class="navbar-collapse collapse navbar-responsive-collapse">
		<ul class="nav navbar-nav" id="dmenu">
 			#foreach ($topMenu in $rootMenu.children)
    			#if ($topMenu.isUserInRoles() || $topMenu.isUserInChildMenuRoles())
       				#if ($topMenu.children.empty)
      					<li class="active">$topMenu</li>
      				#else
         				<li class="dropdown"> 
							$topMenu
	            			<ul class="dropdown-menu">
	           					#foreach ($subMenu in $topMenu.children)
	              					#if ($subMenu.isUserInRoles())
	              						<li class="divider">$subMenu</li>
		            					<li>$subMenu</li>
	             					#end
	           					#end
	           				</ul>
         				</li>
       				#end
				#end
  			#end
		</ul>
	</div>
</div>