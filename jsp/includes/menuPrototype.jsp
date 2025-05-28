<ul style="background-color: #ebebeb;" id="dmenu">
 	#foreach ($topMenu in $rootMenu.children)
     	##if ($topMenu.isUserInRoles() || $topMenu.isUserInChildMenuRoles())
       #if ($topMenu.children.empty)
      	<li class="topitem">
      		$topMenu
      		
      	</li>
      	#else
         	<li class="topitem">$topMenu 
           <ul class="submenu"
           #foreach ($subMenu in $topMenu.children)
              	##if ($subMenu.isUserInRoles())
            ><li> $subMenu        
            </li
             		##end
           	#end
           		>
           	</ul>
         	</li>
       	#end
    		##end
  	#end
</ul>


