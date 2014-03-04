<?php

class Sourcecategories extends DataMapper {
	var $model = 'Sourcecategories';
	var $table = 'sourcecategories';
	
	var $has_one = array();
	var $has_many = array();

	var $validation = array(
		'site' => array(
			'rules' => array('required','valid_match' => array('hh', 'sj', 'rr')),
			'label' => 'Site'
		),
		'site_id' => array(
			'rules' => array('required'),
			'label' => 'SiteId'
		),
		'category_name' => array(
			'rules' => array('required'),
			'label' => 'Category name'
		),
		'parent_id' => array(
			'rules' => array(),
			'label' => 'Parent Id'
		)
	);

	var $default_order_by = array('id' => 'desc');
 

    function __construct($id = NULL)
	{
		parent::__construct($id);
    }

	function post_model_init($from_cache = FALSE)
	{
	}
	// --------------------------------------------------------------------
	// Custom Methods
	//   Add your own custom methods here to enhance the model.
	// --------------------------------------------------------------------

	/* Example Custom Method
	function get_open_templates()
	{
		return $this->where('status <>', 'closed')->get();
	}
	*/

	// --------------------------------------------------------------------
	// Custom Validation Rules
	//   Add custom validation rules for this model here.
	// --------------------------------------------------------------------

	/* Example Rule
	function _convert_written_numbers($field, $parameter)
	{
	 	$nums = array('one' => 1, 'two' => 2, 'three' => 3);
	 	if(in_array($this->{$field}, $nums))
		{
			$this->{$field} = $nums[$this->{$field}];
	 	}
	}
	*/
}

/* End of file template.php */
/* Location: ./application/models/template.php */
