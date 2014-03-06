<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Jobshunter admin</title>
<link href="<?echo base_url()?>assets/css/transdmin.css" rel="stylesheet" type="text/css" media="screen" />
<script type="text/javascript" src="http://code.jquery.com/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="<?echo base_url()?>assets/js/jNice.js"></script>
<?if ($content=='categories') {?>
	<link href="<?echo base_url()?>assets/css/categories.css" rel="stylesheet" type="text/css" media="screen" />
<?}?>

</head>

<body>
	<div id="wrapper">
    	<h1><a href="#"><span>Jobshunter</span></a></h1>
        <ul id="mainNav">
			<?foreach ($main_menu as $menu_item) {?>
				<li <?echo ($menu_item['is_active']?'class="active"':"");?>><a href="<?echo $menu_item['url'];?>"><?echo $menu_item['name'];?></a></li>
			<?}?>
        </ul>
        <div id="containerHolder">
			<div id="container">
        		<div id="sidebar">
                	Sidebar here
                </div>
                <h2>
				<?$first = true;?>
				<?foreach($nav_menu as $navmenu_item) {?>
					<?if (!$first) {?>
						 &raquo; 
					<?}?>
						<a href="<?echo $navmenu_item['url'];?>"><?echo $navmenu_item['name'];?></a>
					<?$first = false;?>
				<?}?>
				</h2>
                
                <div id="main">
                	<?$this->load->view('content');?>
                </div>
                
                <div class="clear"></div>
            </div>
        </div>	
        
        <p id="footer">Jobshunter 2014</p>
    </div>
</body>
</html>