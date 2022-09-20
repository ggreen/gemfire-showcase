# GEDI Geode Spring Security extensions


Add the following to the Spring Configuration


	@Bean
	public UserDetailsService userDetailsService(@Autowired GeodeClient geodeClient)
	{
		return new GeodeUserDetailsService(geodeClient.getRegion("users"));
	}