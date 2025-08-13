package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.fasterxml.jackson.databind.util.ArrayBuilders;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.QExhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Exhibition> findExhibitionsWithFilters(
            Category category, String district, Mood mood, LocalDate localDate,
            ExhibitionSort sort, Pageable pageable) {

        QExhibition exhibition = QExhibition.exhibition;

        // 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder()
                .and(exhibition.isDeleted.isFalse());

        // 카테고리 필터
        if (category != null) {
            builder.and(exhibition.category.eq(category));
        }

        // 지역 필터 (Address 엔티티의 roadAddress 필드 사용)
        if (district != null && !district.trim().isEmpty()) {
            builder.and(exhibition.address.district.equalsIgnoreCase(district.trim()));
        }

        // 분위기 필터
        if (mood != null) {
            builder.and(exhibition.mood.eq(mood));
        }

        // 날짜 필터 (해당 날짜가 전시 시작일~종료일 사이인 경우)
        if (localDate != null) {
            builder.and(exhibition.startDate.loe(localDate)
                    .and(exhibition.endDate.goe(localDate)));
        }

        // 전체 개수 조회 (fetchCount 대신 fetch().size() 사용 - 최신 QueryDSL 버전 호환)
        Long total = queryFactory
                .select(exhibition.count())
                .from(exhibition)
                .where(builder)
                .fetchOne();

        // 정렬, 디폴트 최신순
        OrderSpecifier<?> order = switch (sort == null ? ExhibitionSort.NEW : sort) {
            case OLD     -> exhibition.createdAt.asc();
            case POPULAR -> new OrderSpecifier<>(Order.DESC, exhibition.likeCount, OrderSpecifier.NullHandling.NullsLast);
            case NEW     -> exhibition.createdAt.desc();
        };

        // 페이징된 결과 조회
        List<Exhibition> content = queryFactory
                .selectFrom(exhibition)
                .where(builder)
                .orderBy(order, exhibition.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}